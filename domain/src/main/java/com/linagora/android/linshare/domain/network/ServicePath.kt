package com.linagora.android.linshare.domain.network

import com.linagora.android.linshare.domain.model.document.DocumentId

data class ServicePath(val path: String) {
    init {
        require(!path.startsWith("/")) { "ServicePath must not start with symbol" }
    }

    companion object {
        fun buildDownloadPath(documentId: DocumentId): ServicePath {
            return ServicePath("${Endpoint.DOCUMENT_PATH}/${documentId.uuid}/download")
        }
    }
}
