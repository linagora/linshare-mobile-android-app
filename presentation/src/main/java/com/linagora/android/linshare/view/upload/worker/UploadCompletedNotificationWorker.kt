package com.linagora.android.linshare.view.upload.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.linagora.android.linshare.R
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.RESULT_MESSAGE
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_RESULT
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadCompletedNotificationWorker @Inject constructor(
    private val context: Context,
    private val params: WorkerParameters,
    private val systemNotifier: SystemNotifier,
    private val uploadNotification: UploadAndDownloadNotification
) : CoroutineWorker(context, params) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadCompletedNotificationWorker::class.java)
    }

    override suspend fun doWork(): Result {
        try {
            val uploadResult = inputData.getString(UPLOAD_RESULT)
            val message = inputData.getString(RESULT_MESSAGE)
            require(!uploadResult.isNullOrEmpty())
            require(!message.isNullOrEmpty())

            notifyUploadCompleted(
                uploadResult = UploadResult.valueOf(inputData.getString(UPLOAD_RESULT)!!),
                message = message
            )
        } catch (throwable: Throwable) {
            LOGGER.error("doWork(): $throwable")
            notifyUploadCompleted(UploadResult.UPLOAD_SUCCESS, getFallbackUploadMessage())
        }
        return Result.success()
    }

    private fun getFallbackUploadMessage(): String {
        return context.getString(R.string.upload_success)
    }

    private fun notifyUploadCompleted(uploadResult: UploadResult, message: String) {
        uploadNotification.notify(systemNotifier.generateNotificationId()) {
            this.setContentTitle(getNotificationTitle(uploadResult))
                .setOngoing(BaseNotification.FINISHED_NOTIFICATION)
                .setContentText(message)
                .disableProgressBar()
            this.build()
        }
    }

    private fun getNotificationTitle(uploadResult: UploadResult): String {
        return when (uploadResult) {
            UploadResult.UPLOAD_FAILED -> context.getString(R.string.upload_failed)
            else -> context.getString(R.string.upload_success)
        }
    }

    class Factory @Inject constructor(
        private val systemNotifier: SystemNotifier,
        private val uploadAndDownloadNotification: UploadAndDownloadNotification
    ) : ChildWorkerFactory {
        override fun create(
            applicationContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return UploadCompletedNotificationWorker(
                applicationContext,
                params,
                systemNotifier,
                uploadAndDownloadNotification
            )
        }
    }
}
