package com.linagora.android.linshare.view.upload.worker

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadNotification
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
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
    }

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.computation) {
            val notificationId = systemNotifier.generateNotificationId()
            try {
                val fileUri = Uri.parse(inputData.getString(FILE_URI_INPUT_KEY))
                queryDocumentFromSystemFile(fileUri)!!
                    .let { document ->
                        notifyUploading(
                            notificationId = notificationId,
                            message = document.fileName,
                            max = 0,
                            progress = 0,
                            indeterminate = true
                        )
                        documentRepository.upload(document) { transferredBytes, totalBytes ->
                            notifyUploading(
                                notificationId = notificationId,
                                message = document.fileName,
                                max = totalBytes,
                                progress = transferredBytes,
                                indeterminate = false
                            )
                        }
                        notifyUploadSuccess(notificationId)
                    }
                Result.success()
            } catch (exp: Exception) {
                LOGGER.error(exp.message, exp)
                notifyUploadFailure(notificationId)
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

    private fun notifyUploading(notificationId: NotificationId, message: String, max: TotalBytes, progress: TransferredBytes, indeterminate: Boolean) {
        val percentage = (progress.value * 1f / max.value) * 100
        notifyUploading(notificationId, message, 100, percentage.toInt(), indeterminate)
    }

    private fun notifyUploading(notificationId: NotificationId, message: String, max: Int, progress: Int, indeterminate: Boolean) {
        uploadNotification.apply {
            notify(
                notificationId = notificationId,
                notificationBuilder = {
                    this.setContentTitle(appContext.resources.getQuantityString(R.plurals.uploading_n_file, 1))
                        .setContentText(message)
                        .setProgress(max, progress, indeterminate)
                        .build()
                }
            )
        }
    }

    private fun notifyUploadSuccess(notificationId: NotificationId) {
        uploadNotification.apply {
            notify(notificationId) {
                this.setContentTitle(appContext.getString(R.string.upload_success))
                    .disableProgressBar()
                this.build()
            }
        }
    }

    private fun notifyUploadFailure(notificationId: NotificationId) {
        uploadNotification.apply {
            notify(notificationId) {
                this.setContentTitle(appContext.getString(R.string.upload_failed))
                    .disableProgressBar()
                this.build()
            }
        }
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
