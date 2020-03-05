package com.linagora.android.linshare.view.upload.worker

import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationCompat.Builder
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.network.InternetNotAvailable
import com.linagora.android.linshare.domain.usecases.quota.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.linshare.domain.usecases.upload.UploadInteractor
import com.linagora.android.linshare.domain.usecases.upload.UploadSuccessViewState
import com.linagora.android.linshare.domain.usecases.upload.UploadingViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.model.resources.StringId
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.BaseNotification.Companion.FINISHED_NOTIFICATION
import com.linagora.android.linshare.notification.BaseNotification.Companion.ONGOING_NOTIFICATION
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.UploadAndDownloadNotification.Companion.MAX_UPDATE_PROGRESS_COUNT
import com.linagora.android.linshare.notification.UploadAndDownloadNotification.Companion.REDUCE_RATIO
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.notification.showWaitingProgress
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.widget.makeCustomToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

class UploadWorker(
    private val appContext: Context,
    private val params: WorkerParameters,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val uploadInteractor: UploadInteractor,
    private val systemNotifier: SystemNotifier,
    private val uploadNotification: BaseNotification
) : CoroutineWorker(appContext, params) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadWorker::class.java)

        const val FILE_PATH_INPUT_KEY = "upload_file_path"

        const val FILE_NAME_INPUT_KEY = "upload_file_name"

        const val FILE_MIME_TYPE_INPUT_KEY = "upload_file_mime_type"

        const val TAG_UPLOAD_WORKER = "upload_worker"
    }

    private val currentState = Either.right(Idle)

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.computation) {
            val notificationId = systemNotifier.generateNotificationId()
            var tempUploadFile: File? = null
            try {
                tempUploadFile = File(inputData.getString(FILE_PATH_INPUT_KEY))
                upload(buildDocumentFromInputData(tempUploadFile), notificationId)
                Result.success()
            } catch (throwable: Throwable) {
                LOGGER.error(throwable.message, throwable)
                notifyUploadFailure(
                    notificationId = notificationId,
                    title = StringId(R.string.upload_failed),
                    message = when (throwable) {
                        is UploadException -> handleMessageUploadException(throwable.errorResponse)
                        else -> StringId(R.string.can_not_perform_upload)
                    }
                )
                Result.failure()
            } finally {
                tempUploadFile?.let { FileUtils.deleteQuietly(it) }
            }
        }
    }

    private fun handleMessageUploadException(errorResponse: ErrorResponse): StringId {
        return when (errorResponse) {
            ErrorResponse.FILE_NOT_FOUND -> StringId(R.string.file_not_found)
            else -> StringId(R.string.can_not_perform_upload)
        }
    }

//    private fun queryDocumentFromSystemFile(uri: Uri): DocumentRequest? {
//        return appContext.contentResolver.query(uri, null, null, null, null)
//            ?.use { cursor -> extractCursor(cursor, uri)
//                ?: throw UploadException(ErrorResponse.FILE_NOT_FOUND)
//            }
//    }

//    private fun extractCursor(cursor: Cursor, uri: Uri): DocumentRequest? {
//        return takeIf { cursor.moveToFirst() }
//            ?.let { cursor.getDocumentRequest(uri) }
    private fun buildDocumentFromInputData(file: File): DocumentRequest {
        return DocumentRequest(
            file = file,
            uploadFileName = inputData.getString(FILE_NAME_INPUT_KEY)!!,
            mediaType = inputData.getString(FILE_MIME_TYPE_INPUT_KEY)!!.toMediaType()
        )
    }

    private suspend fun setWaitingForeground(notificationId: NotificationId, message: String) {
        setForeground(ForegroundInfo(notificationId.value, uploadNotification.create {
            configUploadingNotificationBuilder(this)
                .setContentText(message)
                .showWaitingProgress()
                .build()
        }))
    }

    private suspend fun upload(
        document: DocumentRequest,
        notificationId: NotificationId
    ) {
        uploadInteractor(document)
            .collect { state ->
                collectUploadState(notificationId, document, state)
            }
    }

    private suspend fun collectUploadState(notificationId: NotificationId, document: DocumentRequest, state: State<Either<Failure, Success>>) {
        state(currentState).fold(
            ifLeft = { failure -> notifyOnFailureState(systemNotifier.generateNotificationId(), document, failure) },
            ifRight = { success -> notifyOnSuccessState(notificationId, document, success) }
        )
    }

    private fun notifyOnFailureState(notificationId: NotificationId, document: DocumentRequest, failure: Failure) {
        when (failure) {
            QuotaAccountNoMoreSpaceAvailable -> notifyUploadFailure(
                notificationId = notificationId,
                title = document.uploadFileName,
                message = appContext.getString(R.string.no_more_space_avalable)
            )
            InternetNotAvailable -> notifyUploadFailure(
                notificationId = notificationId,
                title = appContext.getString(R.string.you_are_offline),
                message = String.format(appContext.getString(R.string.internet_not_available), document.uploadFileName)
            )
            else -> notifyUploadFailure(
                notificationId = notificationId,
                title = appContext.getString(R.string.upload_failed),
                message = document.uploadFileName
            )
        }
    }

    private suspend fun notifyOnSuccessState(notificationId: NotificationId, document: DocumentRequest, success: Success) {
        when (success) {
            Loading -> {
                alertDownLoading(document.uploadFileName)
                setWaitingForeground(notificationId, document.uploadFileName)
            }
            is UploadingViewState -> {
                updateUploadingProgress(document, notificationId, success.transferredBytes, success.totalBytes)
            }
            is UploadSuccessViewState -> {
                notifyUploadSuccess(systemNotifier.generateNotificationId(), document.uploadFileName)
            }
        }
    }

    private suspend fun alertDownLoading(fileName: String) {
        withContext(dispatcherProvider.main) {
            Toast(appContext).makeCustomToast(appContext, String.format(appContext.resources.getString(R.string.file_ready_to_upload), fileName), Toast.LENGTH_LONG).show()
        }
    }

    private fun configUploadingNotificationBuilder(builder: Builder): Builder {
        return builder.setContentTitle(appContext.resources.getQuantityString(R.plurals.notify_count_file_upload, 1))
    }

    private suspend fun setUploadingForeground(
        notificationId: NotificationId,
        message: String,
        max: TotalBytes,
        progress: TransferredBytes
    ) {
        val percentage = (progress.value * 1f / max.value) * 100
        return setUploadingForeground(notificationId, message, 100, percentage.toInt())
    }

    private suspend fun setUploadingForeground(
        notificationId: NotificationId,
        message: String,
        max: Int,
        progress: Int
    ) {
        setForeground(ForegroundInfo(notificationId.value, uploadNotification.create {
            configUploadingNotificationBuilder(this)
                .setContentText(message)
                .setProgress(max, progress, BaseNotification.DISABLE_PROGRESS_INDETERMINATE)
                .setOngoing(ONGOING_NOTIFICATION)
                .build()
        }))
    }

    private fun reduceUpdateProgress(transferredBytes: TransferredBytes): Int {
        return (transferredBytes.value % REDUCE_RATIO).toInt()
    }

    private suspend fun updateUploadingProgress(
        document: DocumentRequest,
        notificationId: NotificationId,
        transferredBytes: TransferredBytes,
        totalBytes: TotalBytes
    ) {
        reduceUpdateProgress(transferredBytes)
            .takeIf { it < MAX_UPDATE_PROGRESS_COUNT }
            ?.let {
                withContext(dispatcherProvider.main) {
                    setUploadingForeground(
                        notificationId = notificationId,
                        message = document.uploadFileName,
                        max = totalBytes,
                        progress = transferredBytes
                    )
                }
            }
    }

    private fun notifyUploadSuccess(notificationId: NotificationId, message: String) {
        uploadNotification.notify(notificationId) {
            this.setContentTitle(appContext.getString(R.string.upload_success))
                .setOngoing(FINISHED_NOTIFICATION)
                .disableProgressBar()
            this.build()
        }
    }

    private fun notifyUploadFailure(notificationId: NotificationId, title: StringId, message: StringId) {
        notifyUploadFailure(
            notificationId = notificationId,
            title = appContext.getString(title.value),
            message = appContext.getString(message.value)
        )
    }

    private fun notifyUploadFailure(notificationId: NotificationId, title: String, message: String) {
        uploadNotification.notify(notificationId) {
            this.setContentTitle(title)
                .setContentText(message)
                .setOngoing(FINISHED_NOTIFICATION)
                .disableProgressBar()
            this.build()
        }
    }

    class Factory @Inject constructor(
        private val dispatcherProvider: CoroutinesDispatcherProvider,
        private val uploadInteractor: UploadInteractor,
        private val systemNotifier: SystemNotifier,
        private val uploadAndDownloadNotification: UploadAndDownloadNotification
    ) : ChildWorkerFactory {
        override fun create(
            applicationContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return UploadWorker(
                applicationContext,
                params,
                dispatcherProvider,
                uploadInteractor,
                systemNotifier,
                uploadAndDownloadNotification
            )
        }
    }
}
