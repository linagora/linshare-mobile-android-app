package com.linagora.android.linshare.domain.model

data class SystemErrorCode(override val value: Int) : BaseErrorCode() {
    init {
        require(value >= 0) { "systemErrorCode must not be negative" }
    }
}
