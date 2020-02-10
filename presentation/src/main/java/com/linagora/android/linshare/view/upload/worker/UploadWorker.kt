package com.linagora.android.linshare.view.upload.worker

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.app.NotificationCompat.Builder
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.BaseNotification.Companion.FINISHED_NOTIFICATION
import com.linagora.android.linshare.notification.BaseNotification.Companion.ONGOING_NOTIFICATION
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadNotification
import com.linagora.android.linshare.notification.UploadNotification.Companion.MAX_UPDATE_PROGRESS_COUNT
import com.linagora.android.linshare.notification.UploadNotification.Companion.REDUCE_RATIO
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.notification.showWaitingProgress
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadWorker(
    private val appContext: Context,
    private val params: WorkerParameters,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val documentRepository: DocumentRepository,
    private val systemNotifier: SystemNotifier,
    private val uploadNotification: BaseNotification
) : CoroutineWorker(appContext, params) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadWorker::class.java)

        const val FILE_URI_INPUT_KEY = "upload_file_uri"

        const val TAG_UPLOAD_WORKER = "upload_worker"
    }

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.computation) {
            val notificationId = systemNotifier.generateNotificationId()
            try {
                val fileUri = Uri.parse(inputData.getString(FILE_URI_INPUT_KEY))
                queryDocumentFromSystemFile(fileUri)!!
                    .let { document ->
                        setWaitingForeground(notificationId, document.fileName)
                        upload(this, document, notificationId)
                    }
                setUploadSuccessForeground(notificationId)
                Result.success()
            } catch (throwable: Throwable) {
                LOGGER.error(throwable.message, throwable)
                setUploadFailureForeground(notificationId)
                Result.failure()
            }
        }
    }

    private fun queryDocumentFromSystemFile(uri: Uri): DocumentRequest? {
        return appContext.contentResolver.query(uri, null, null, null, null)
            ?.use { cursor ->
                with(cursor) {
                    moveToFirst()
                    val fileName = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val size = getLong(getColumnIndex(OpenableColumns.SIZE))
                    val mimeType = getString(getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    DocumentRequest(uri, fileName, size, mimeType.toMediaType())
                }
            }
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
        coroutineScope: CoroutineScope,
        document: DocumentRequest,
        notificationId: NotificationId
    ) {
        documentRepository.upload(document) { transferredBytes, totalBytes ->
            updateUploadingProgress(coroutineScope, document, notificationId, transferredBytes, totalBytes)
        }
    }

    private fun configUploadingNotificationBuilder(builder: Builder): Builder {
        return builder.setContentTitle(appContext.resources.getQuantityString(R.plurals.uploading_n_file, 1))
    }

    private suspend fun setUploadingForeground(
        notificationId: NotificationId,
        message: String,
        max: TotalBytes,
        progress: TransferredBytes
    ) {
        val percentage = (progress.value / max.value) * 100
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

    private fun updateUploadingProgress(
        coroutineScope: CoroutineScope,
        document: DocumentRequest,
        notificationId: NotificationId,
        transferredBytes: TransferredBytes,
        totalBytes: TotalBytes
    ) {
        reduceUpdateProgress(transferredBytes)
            .takeIf { it < MAX_UPDATE_PROGRESS_COUNT }
            ?.let {
                coroutineScope.launch {
                    setUploadingForeground(
                        notificationId = notificationId,
                        message = document.fileName,
                        max = totalBytes,
                        progress = transferredBytes
                    )
                }
            }
    }

    private suspend fun setUploadSuccessForeground(notificationId: NotificationId) {
        setForeground(ForegroundInfo(notificationId.value, uploadNotification.create {
            this.setContentTitle(appContext.getString(R.string.upload_success))
                .setOngoing(FINISHED_NOTIFICATION)
                .disableProgressBar()
            this.build()
        }))
    }

    private suspend fun setUploadFailureForeground(notificationId: NotificationId) {
        setForeground(ForegroundInfo(notificationId.value, uploadNotification.create {
            this.setContentTitle(appContext.getString(R.string.upload_failed))
                .setOngoing(FINISHED_NOTIFICATION)
                .disableProgressBar()
            this.build()
        }))
    }

    class Factory @Inject constructor(
        private val dispatcherProvider: CoroutinesDispatcherProvider,
        private val documentRepository: DocumentRepository,
        private val systemNotifier: SystemNotifier,
        private val uploadNotification: UploadNotification
    ) : ChildWorkerFactory {
        override fun create(
            applicationContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return UploadWorker(
                applicationContext,
                params,
                dispatcherProvider,
                documentRepository,
                systemNotifier,
                uploadNotification
            )
        }
    }
}
