package com.linagora.android.linshare.domain.network

data class ServicePath(val path: String) {
    init {
        require(!path.startsWith("/")) { "ServicePath must not start with symbol" }
    }
}
