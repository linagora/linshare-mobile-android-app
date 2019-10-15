package com.linagora.android.linshare.domain.model

data class Password(val value: String) {
    init {
        require(value.isNotBlank()) { "password is invalid" }
    }
}
