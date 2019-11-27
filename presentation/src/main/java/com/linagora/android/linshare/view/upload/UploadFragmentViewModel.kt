package com.linagora.android.linshare.view.upload

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadFragmentViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(UploadFragmentViewModel::class.java)
    }

    fun upload(documentRequest: DocumentRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            LOGGER.info("launch")
            val document = documentRepository.upload(documentRequest)
            LOGGER.info("document: $document")
        }
    }
}
