package com.linagora.android.linshare.domain.model

data class ClientErrorCode(override val value: Int) : BaseErrorCode() {
    init {
        require(value >= 0) { "clientErrorCode must not be negative" }
    }
}
