package com.linagora.android.linshare.domain.network

object Endpoint {

    const val ROOT_PATH = "linshare/webservice/rest/user/v2"

    const val AUTHENTICATION_PATH = "$ROOT_PATH/jwt"

    const val DOCUMENT_PATH = "$ROOT_PATH/documents"

    const val RECEIVED_SHARES_PATH = "$ROOT_PATH/received_shares"

    const val SHARED_SPACE_PATH = "$ROOT_PATH/shared_spaces"

    const val NODES = "nodes"

    const val DOWNLOAD = "download"

    val AUTHENTICAION = ServicePath(AUTHENTICATION_PATH)
}
