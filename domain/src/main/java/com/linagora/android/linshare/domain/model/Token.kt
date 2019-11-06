package com.linagora.android.linshare.domain.model

data class Token(val token: String) {
    init {
        require(token.isNotBlank()) { "token is invalid" }
    }

    fun asBearerHeader() = "Bearer $token"
}
