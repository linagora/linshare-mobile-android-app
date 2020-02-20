package com.linagora.android.linshare.domain.network

import java.util.UUID

data class ServicePath(val path: String) {
    init {
        require(!path.startsWith("/")) { "ServicePath must not start with symbol" }
    }

    companion object {
        fun buildDownloadPath(documentUuid: UUID): ServicePath {
            return ServicePath("${Endpoint.DOCUMENT_PATH}/$documentUuid/download")
        }
    }
}
