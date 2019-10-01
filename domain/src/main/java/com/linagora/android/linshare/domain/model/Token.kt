package com.linagora.android.linshare.domain.model

data class Token(val tokenString: String) {
    init {
        require(tokenString.isNotBlank()) { "token is invalid" }
    }
}
