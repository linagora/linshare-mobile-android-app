package com.linagora.android.linshare.domain.model

data class ErrorCode(val value: Int) {
    init {
        require(value >= 0) { "errorCode must not be negative" }
    }
}
