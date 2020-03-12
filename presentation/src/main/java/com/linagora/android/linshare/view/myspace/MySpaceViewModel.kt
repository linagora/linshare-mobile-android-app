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
import com.linagora.android.linshare.domain.model.download.DownloadingTask
import com.linagora.android.linshare.domain.model.download.EnqueuedDownloadId
import com.linagora.android.linshare.domain.network.ServicePath
import com.linagora.android.linshare.domain.network.withServicePath
import com.linagora.android.linshare.domain.repository.download.DownloadingRepository
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.domain.usecases.myspace.DownloadClick
import com.linagora.android.linshare.domain.usecases.myspace.GetAllDocumentsInteractor
import com.linagora.android.linshare.domain.usecases.myspace.RemoveClick
import com.linagora.android.linshare.domain.usecases.myspace.SearchButtonClick
import com.linagora.android.linshare.domain.usecases.myspace.UploadButtonBottomBarClick
import com.linagora.android.linshare.domain.usecases.remove.RemoveDocumentInteractor
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.LinShareApplication
import com.linagora.android.linshare.view.base.LinShareViewModel
import com.linagora.android.linshare.view.base.ListItemBehavior
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MySpaceViewModel @Inject constructor(
    application: LinShareApplication,
    private val getAllDocumentsInteractor: GetAllDocumentsInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val uploadAndDownloadNotification: UploadAndDownloadNotification,
    private val systemNotifier: SystemNotifier,
    private val downloadingRepository: DownloadingRepository,
    private val removeDocumentInteractor: RemoveDocumentInteractor
) : LinShareViewModel(application, dispatcherProvider), ListItemBehavior<Document> {

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

    override fun onContextMenuClick(document: Document) {
        LOGGER.info("onContextMenuClick() $document")
        dispatchState(Either.right(ContextMenuClick(document)))
    }

    fun onDownloadClick(document: Document) {
        LOGGER.info("onDownloadClick() $document")
        setProcessingDocument(document)
        dispatchState(Either.right(DownloadClick(document)))
    }

    fun onUploadBottomBarClick() {
        LOGGER.info("onUploadBottomBarClick()")
        dispatchState(Either.right(UploadButtonBottomBarClick))
    }

    fun onRemoveClick(document: Document) {
        dispatchState(Either.right(RemoveClick(document)))
    }

    fun onSearchButtonClick() {
        LOGGER.info("onSearchButtonClick()")
        dispatchState(Either.right(SearchButtonClick))
    }

    private fun setProcessingDocument(document: Document) {
        downloadingDocument.value = document
    }

    fun getDownloadingDocument(): Document? {
        return downloadingDocument.value
    }

    fun removeDocument(document: Document) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(removeDocumentInteractor(document.documentId))
        }
    }

    fun downloadDocument(credential: Credential, token: Token, document: Document) {
        LOGGER.info("downloadDocument() $document")
        try {
            val downloadUri = Uri.parse(credential.serverUrl
                .withServicePath(ServicePath.buildDownloadPath(document.documentId))
                .toString())
            val request = DownloadManager.Request(downloadUri)
                .addRequestHeader("Authorization", "Bearer ${token.token}")
                .setTitle(document.name)
                .setDescription(application.getString(R.string.app_name))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, document.name)

            (application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                .enqueue(request)
                .let(::EnqueuedDownloadId)
                .also { storeDownloadTask(it, document) }
        } catch (exp: Exception) {
            LOGGER.error("downloadDocument() $exp - ${exp.printStackTrace()}")
            notifyDownloadFailure(
                notificationId = systemNotifier.generateNotificationId(),
                title = document.name,
                message = application.getString(R.string.download_failed)
            )
        }
    }

    private fun storeDownloadTask(enqueuedDownloadId: EnqueuedDownloadId, document: Document) {
        viewModelScope.launch(dispatcherProvider.io) {
            downloadingRepository.storeTask(DownloadingTask(
                enqueuedDownloadId = enqueuedDownloadId,
                documentName = document.name,
                documentSize = document.size,
                documentId = document.documentId,
                mediaType = document.type
            ))
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
