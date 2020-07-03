package com.linagora.android.linshare.operator.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.error.NotEnoughFreeDeviceStorageException
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.download.DownloadingTask
import com.linagora.android.linshare.domain.model.download.EnqueuedDownloadId
import com.linagora.android.linshare.domain.network.withServicePath
import com.linagora.android.linshare.domain.repository.download.DownloadingRepository
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.DeviceStorageStats
import com.linagora.android.linshare.util.DeviceStorageStats.Companion.INTERNAL_ROOT
import com.linagora.android.linshare.util.NetworkConnectivity
import com.linagora.android.linshare.view.widget.makeCustomToast
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import javax.inject.Inject

class DownloadManagerOperator @Inject constructor(
    private val context: Context,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val uploadAndDownloadNotification: UploadAndDownloadNotification,
    private val systemNotifier: SystemNotifier,
    private val downloadingRepository: DownloadingRepository,
    private val deviceStorageStats: DeviceStorageStats,
    private val internetAvailable: ConnectionLiveData
) : DownloadOperator {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DownloadManagerOperator::class.java)
    }

    override suspend fun download(credential: Credential, token: Token, downloadRequest: DownloadRequest) {
        LOGGER.info("download() $downloadRequest")
        try {
            preCheckDownloadRequest(downloadRequest)
            execute(credential, token, downloadRequest)
            alertDownloadInWaitingList()
        } catch (exp: Exception) {
            LOGGER.error("download() $exp - ${exp.printStackTrace()}")
            notifyDownloadOnFailure(downloadRequest, exp)
        }
    }

    private fun preCheckDownloadRequest(downloadRequest: DownloadRequest) {
        val deviceFreeSpace = deviceStorageStats.getDeviceStorageFreeSpace(INTERNAL_ROOT)
        if (downloadRequest.downloadSize >= deviceFreeSpace) {
            throw NotEnoughFreeDeviceStorageException
        }
    }

    private suspend fun execute(credential: Credential, token: Token, downloadRequest: DownloadRequest) {
        val downloadUri = Uri.parse(credential.serverUrl
            .withServicePath(downloadRequest.toServicePath())
            .toString())
        val request = DownloadManager.Request(downloadUri)
            .addRequestHeader("Authorization", "Bearer ${token.token}")
            .setTitle(downloadRequest.downloadName)
            .setDescription(context.getString(R.string.app_name))
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadRequest.downloadName)

        (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
            .enqueue(request)
            .let(::EnqueuedDownloadId)
            .also { storeDownloadTask(it, downloadRequest) }
    }

    private suspend fun storeDownloadTask(enqueuedDownloadId: EnqueuedDownloadId, downloadRequest: DownloadRequest) {
        downloadingRepository.storeTask(
            DownloadingTask(
                enqueuedDownloadId = enqueuedDownloadId,
                downloadName = downloadRequest.downloadName,
                downloadSize = downloadRequest.downloadSize,
                downloadDataId = downloadRequest.downloadDataId,
                mediaType = downloadRequest.downloadMediaType,
                downloadType = downloadRequest.downloadType,
                sharedSpaceId = downloadRequest.sharedSpaceId
            )
        )
    }

    private suspend fun alertDownloadInWaitingList() {
        withContext(dispatcherProvider.main) {
            internetAvailable.value
                ?.takeIf { it == NetworkConnectivity.DISCONNECTED }
                ?.let {
                    Toast(context)
                        .makeCustomToast(context, context.getString(R.string.file_ready_for_download_once_connection_available), Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun notifyDownloadOnFailure(downloadRequest: DownloadRequest, exception: Exception) {
        LOGGER.error("notifyDownloadOnFailure(): $exception")
        val messageId = when (exception) {
            NotEnoughFreeDeviceStorageException -> R.string.error_insufficient_space
            else -> R.string.download_failed
        }

        notifyDownloadFailure(
            notificationId = systemNotifier.generateNotificationId(),
            title = downloadRequest.downloadName,
            message = context.getString(messageId)
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
