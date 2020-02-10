package com.linagora.android.linshare.domain.model

data class LinShareErrorCode(val value: Int) {
    init {
        require(value >= 0) { "linShareErrorCode must not be negative" }
    }
}
