package com.linagora.android.linshare.domain.model

data class Username(val username: String) {
    init {
        require(username.isNotBlank()) { "username is invalid" }
    }
}
