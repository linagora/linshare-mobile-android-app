package com.linagora.android.linshare.operator.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.download.DownloadingTask
import com.linagora.android.linshare.domain.model.download.EnqueuedDownloadId
import com.linagora.android.linshare.domain.network.ServicePath
import com.linagora.android.linshare.domain.network.withServicePath
import com.linagora.android.linshare.domain.repository.download.DownloadingRepository
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.disableProgressBar
import org.slf4j.LoggerFactory
import javax.inject.Inject

class DownloadManagerOperator @Inject constructor(
    private val context: Context,
    private val uploadAndDownloadNotification: UploadAndDownloadNotification,
    private val systemNotifier: SystemNotifier,
    private val downloadingRepository: DownloadingRepository
) : DownloadOperator {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DownloadManagerOperator::class.java)
    }

    override suspend fun downloadDocument(credential: Credential, token: Token, document: Document) {
        LOGGER.info("downloadDocument() $document")

        try {
            val downloadUri = Uri.parse(credential.serverUrl
                .withServicePath(ServicePath.buildDownloadPath(document.documentId))
                .toString())
            val request = DownloadManager.Request(downloadUri)
                .addRequestHeader("Authorization", "Bearer ${token.token}")
                .setTitle(document.name)
                .setDescription(context.getString(R.string.app_name))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, document.name)

            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                .enqueue(request)
                .let(::EnqueuedDownloadId)
                .also { storeDownloadTask(it, document) }
        } catch (exp: Exception) {
            LOGGER.error("downloadDocument() $exp - ${exp.printStackTrace()}")
            notifyDownloadFailure(
                notificationId = systemNotifier.generateNotificationId(),
                title = document.name,
                message = context.getString(R.string.download_failed)
            )
        }
    }

    private suspend fun storeDownloadTask(enqueuedDownloadId: EnqueuedDownloadId, document: Document) {
        downloadingRepository.storeTask(
            DownloadingTask(
                enqueuedDownloadId = enqueuedDownloadId,
                documentName = document.name,
                documentSize = document.size,
                documentId = document.documentId,
                mediaType = document.type
            )
        )
    }

    private fun notifyDownloadFailure(notificationId: NotificationId, title: String, message: String) {
        uploadAndDownloadNotification.notify(notificationId) {
            this.setContentTitle(title)
                .setContentText(message)
                .setOngoing(BaseNotification.FINISHED_NOTIFICATION)
                .disableProgressBar()
            this.build()
        }
    }
}
