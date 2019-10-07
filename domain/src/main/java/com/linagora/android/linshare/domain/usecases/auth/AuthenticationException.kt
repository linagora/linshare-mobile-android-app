package com.linagora.android.linshare.domain.usecases.auth

sealed class AuthenticationException(message: String) : RuntimeException(message) {

    companion object {
        const val EMPTY_TOKEN = "Empty token"

        const val WRONG_CREDENTIAL = "Credential is wrong"

        const val WRONG_PASSWORD = "Password is wrong"
    }
}

data class BadCredentials(override val message: String) : AuthenticationException(message)
object EmptyToken : AuthenticationException(EMPTY_TOKEN)
