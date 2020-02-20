package com.linagora.android.linshare.view.myspace

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.network.ServicePath
import com.linagora.android.linshare.domain.network.withServicePath
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.domain.usecases.myspace.GetAllDocumentsInteractor
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.LinShareApplication
import com.linagora.android.linshare.view.base.LinShareViewModel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MySpaceViewModel @Inject constructor(
    application: LinShareApplication,
    private val getAllDocumentsInteractor: GetAllDocumentsInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val uploadAndDownloadNotification: UploadAndDownloadNotification,
    private val systemNotifier: SystemNotifier
) : LinShareViewModel(application, dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MySpaceViewModel::class.java)
    }

    private val downloadingDocument = MutableLiveData<Document>()

    fun getAllDocuments() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAllDocumentsInteractor())
        }
    }

    fun onSwipeRefresh() {
        getAllDocuments()
    }

    fun onContextMenuClick(document: Document) {
        LOGGER.info("onContextMenuClick() $document")
        setProcessingDocument(document)
        dispatchState(Either.right(ContextMenuClick(document)))
    }

    private fun setProcessingDocument(document: Document) {
        downloadingDocument.value = document
    }

    fun getDownloadingDocument(): Document? {
        return downloadingDocument.value
    }

    fun downloadDocument(credential: Credential, token: Token, document: Document) {
        LOGGER.info("downloadDocument() $document")
        try {
            val downloadUri = Uri.parse(credential.serverUrl
                    .withServicePath(ServicePath.buildDownloadPath(document.uuid))
                    .toString())
            val request = DownloadManager.Request(downloadUri)
                .addRequestHeader("Authorization", "Bearer ${token.token}")
                .setTitle(document.name)
                .setDescription(application.getString(R.string.app_name))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, document.name)

            (application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                .enqueue(request)
        } catch (exp: Exception) {
            LOGGER.error("downloadDocument() $exp - ${exp.printStackTrace()}")
            notifyDownloadFailure(
                notificationId = systemNotifier.generateNotificationId(),
                title = document.name,
                message = application.getString(R.string.download_failed)
            )
        }
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
