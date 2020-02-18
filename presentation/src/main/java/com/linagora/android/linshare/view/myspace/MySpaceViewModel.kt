package com.linagora.android.linshare.view.myspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.domain.usecases.myspace.GetAllDocumentsInteractor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MySpaceViewModel @Inject constructor(
    private val getAllDocumentsInteractor: GetAllDocumentsInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

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
}
