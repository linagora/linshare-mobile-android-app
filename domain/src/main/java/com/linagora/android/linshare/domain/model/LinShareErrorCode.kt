package com.linagora.android.linshare.domain.model

data class LinShareErrorCode(override val value: Int): BaseErrorCode() {
    init {
        require(value >= 0) { "linShareErrorCode must not be negative" }
    }
}
