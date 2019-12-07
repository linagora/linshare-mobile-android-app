package com.linagora.android.linshare.domain.model.upload

data class TransferredBytes(val value: Long) {
    init {
        require(value >= 0) { "Transferred bytes must not be negative" }
    }
}
