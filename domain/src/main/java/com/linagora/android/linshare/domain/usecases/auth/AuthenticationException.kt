package com.linagora.android.linshare.domain.usecases.auth

sealed class AuthenticationException(message: String) : RuntimeException(message) {

    companion object {
        const val EMPTY_TOKEN = "Empty token"

        const val WRONG_CREDENTIAL = "Credential is wrong"

        const val WRONG_PASSWORD = "Password is wrong"

        const val SERVER_NOT_FOUND = "Server not found"

        const val CONNECT_ERROR = "Connect error"

        const val UNKNOWN = "Unknown error"
    }
}

data class BadCredentials(override val message: String) : AuthenticationException(message)
object EmptyToken : AuthenticationException(EMPTY_TOKEN)
object ServerNotFound : AuthenticationException(SERVER_NOT_FOUND)
object ConnectError : AuthenticationException(CONNECT_ERROR)
object UnknownError : AuthenticationException(UNKNOWN)
