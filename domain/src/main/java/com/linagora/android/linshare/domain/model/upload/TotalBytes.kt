package com.linagora.android.linshare.domain.model.upload

data class TotalBytes(val value: Long) {
    init {
        require(value >= 0) { "Total bytes must not be negative" }
    }
}
