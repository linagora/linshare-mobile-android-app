package com.linagora.android.linshare.domain.network

object Endpoint {

    const val ROOT_PATH = "linshare/webservice/rest/user/v2"

    const val AUTHENTICATION_PATH = "$ROOT_PATH/authentication/jwt"

    val AUTHENTICAION = ServicePath(AUTHENTICATION_PATH)
}
