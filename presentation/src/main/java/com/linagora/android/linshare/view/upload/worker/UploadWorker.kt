package com.linagora.android.linshare.view.upload.worker

import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationCompat.Builder
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.linshare.domain.usecases.upload.UploadFailed
import com.linagora.android.linshare.domain.usecases.upload.UploadSuccess
import com.linagora.android.linshare.domain.usecases.upload.UploadingViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.model.resources.StringId
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.BaseNotification.Companion.FINISHED_NOTIFICATION
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.UploadAndDownloadNotification.Companion.MAX_UPDATE_PROGRESS_COUNT
import com.linagora.android.linshare.notification.UploadAndDownloadNotification.Companion.REDUCE_RATIO
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.notification.showWaitingProgress
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.DOCUMENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.RECIPIENTS_KEY
import com.linagora.android.linshare.view.upload.controller.UploadCommand
import com.linagora.android.linshare.view.upload.controller.UploadController
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
    private val systemNotifier: SystemNotifier,
    private val uploadNotification: BaseNotification,
    private val authorizationManager: AuthorizationManager,
    private val dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val uploadController: UploadController,
    private val viewStateStore: ViewStateStore
) : CoroutineWorker(appContext, params) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadWorker::class.java)

        const val FILE_PATH_INPUT_KEY = "upload_file_path"

        const val FILE_NAME_INPUT_KEY = "upload_file_name"

        const val FILE_MIME_TYPE_INPUT_KEY = "upload_file_mime_type"

        const val UPLOAD_REQUEST_TYPE = "upload_request_type"

        const val TAG_UPLOAD_WORKER = "upload_worker"

        const val RESULT_MESSAGE = "upload_result_message"

        const val UPLOAD_RESULT = "upload_result"
    }

    override suspend fun doWork(): Result {
        val notificationId = systemNotifier.generateNotificationId()
        setWaitingForeground(notificationId, appContext.getString(R.string.preparing))
        return withContext(dispatcherProvider.computation) {
            var tempUploadFile: File? = null
            try {
                tempUploadFile = File(inputData.getString(FILE_PATH_INPUT_KEY)!!)
                val documentRequest = buildDocumentFromInputData(tempUploadFile)

                upload(
                    uploadCommand = createUploadCommand(documentRequest),
                    notificationId = notificationId
                )

                getUploadCompletedResult(documentRequest)
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

    private fun createUploadCommand(documentRequest: DocumentRequest): UploadCommand {
        return uploadController.createUploadCommand(documentRequest)
    }

    private suspend fun upload(uploadCommand: UploadCommand, notificationId: NotificationId) {
        uploadController.upload(uploadCommand)
            .collect { state -> collectUploadState(notificationId, uploadCommand.documentRequest, state) }
    }

    private suspend fun collectUploadState(
        notificationId: NotificationId,
        document: DocumentRequest,
        state: State<Either<Failure, Success>>
    ) {
        viewStateStore.storeAndGet(state).fold(
            ifLeft = { Unit },
            ifRight = { success -> reactOnSuccessState(notificationId, document, success) }
        )
    }

    private fun getUploadCompletedResult(document: DocumentRequest): Result {
        return viewStateStore.getCurrentState().fold(
            ifLeft = { getFailureResult(it, document) },
            ifRight = { getSuccessResult(it, document) }
        )
    }

    private fun getFailureResult(failure: Failure, document: DocumentRequest): Result {
        LOGGER.info("getFailureResult(): $failure")
        val failedMessage = when (failure) {
            is UploadFailed -> failure.message
            else -> document.uploadFileName
        }
        return Result.success(workDataOf(
            RESULT_MESSAGE to failedMessage,
            UPLOAD_RESULT to UploadResult.UPLOAD_FAILED.name,
            UPLOAD_REQUEST_TYPE to inputData.getString(UPLOAD_REQUEST_TYPE)
        ))
    }

    private fun getSuccessResult(success: Success, documentRequest: DocumentRequest): Result {
        LOGGER.info("getSuccessResult(): $success")
        return Result.success(workDataOf(
            UPLOAD_RESULT to UploadResult.UPLOAD_SUCCESS.name,
            RESULT_MESSAGE to getSuccessMessage(success, documentRequest),
            UPLOAD_REQUEST_TYPE to inputData.getString(UPLOAD_REQUEST_TYPE),
            DOCUMENTS_KEY to listOf(getUploadedDocument(success)?.documentId?.uuid.toString()).toTypedArray(),
            RECIPIENTS_KEY to inputData.getStringArray(RECIPIENTS_KEY)
        ))
    }

    private fun getSuccessMessage(success: Success, documentRequest: DocumentRequest): String {
        return when (success) {
            is UploadSuccess -> success.message
            else -> documentRequest.uploadFileName
        }
    }

    private fun getUploadedDocument(success: Success): Document? {
        return success.takeIf { it is UploadSuccess }
            ?.let { it as UploadSuccess }
            ?.document
    }

    private suspend fun reactOnSuccessState(notificationId: NotificationId, document: DocumentRequest, success: Success) {
        when (success) {
            is AuthenticationViewState -> setUpInterceptors(success)
            Loading -> alertDownLoading(document.uploadFileName)
            is UploadingViewState -> updateUploadingProgress(document, notificationId, success.transferredBytes, success.totalBytes)
        }
    }

    private suspend fun alertDownLoading(fileName: String) {
        withContext(dispatcherProvider.main) {
            Toast(appContext).makeCustomToast(appContext, String.format(appContext.resources.getString(R.string.file_ready_to_upload), fileName), Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpInterceptors(authenticationViewState: AuthenticationViewState) {
        LOGGER.info("setUpInterceptors()")
        dynamicBaseUrlInterceptor.changeBaseUrl(authenticationViewState.credential.serverUrl)
        authorizationManager.updateToken(authenticationViewState.token)
    }

    private fun configUploadingNotificationBuilder(builder: Builder): Builder {
        return builder.setContentTitle(appContext.resources.getQuantityString(R.plurals.notify_count_file_upload, 1))
    }

    private fun notifyUploading(notificationId: NotificationId, message: String, max: TotalBytes, progress: TransferredBytes) {
        val percentage = (progress.value * 1f / max.value) * 100
        uploadNotification.notify(notificationId) {
            this.setContentText(message)
                .setProgress(100, percentage.toInt(), BaseNotification.DISABLE_PROGRESS_INDETERMINATE)
                .build()
        }
    }

    private fun reduceUpdateProgress(transferredBytes: TransferredBytes): Int {
        return (transferredBytes.value % REDUCE_RATIO).toInt()
    }

    private fun updateUploadingProgress(
        document: DocumentRequest,
        notificationId: NotificationId,
        transferredBytes: TransferredBytes,
        totalBytes: TotalBytes
    ) {
        LOGGER.info("updateUploadingProgress(): $transferredBytes/$totalBytes")
        reduceUpdateProgress(transferredBytes)
            .takeIf { it < MAX_UPDATE_PROGRESS_COUNT }
            ?.let { notifyUploading(notificationId, document.uploadFileName, totalBytes, transferredBytes) }
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
        private val systemNotifier: SystemNotifier,
        private val uploadAndDownloadNotification: UploadAndDownloadNotification,
        private val authorizationManager: AuthorizationManager,
        private val dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
        private val uploadController: UploadController,
        private val viewStateStore: ViewStateStore
    ) : ChildWorkerFactory {
        override fun create(
            applicationContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return UploadWorker(
                applicationContext,
                params,
                dispatcherProvider,
                systemNotifier,
                uploadAndDownloadNotification,
                authorizationManager,
                dynamicBaseUrlInterceptor,
                uploadController,
                viewStateStore
            )
        }
    }
}
