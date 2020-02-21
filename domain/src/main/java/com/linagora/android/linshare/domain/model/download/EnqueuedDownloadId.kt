package com.linagora.android.linshare.domain.model.download

data class EnqueuedDownloadId(val value: Int) {
    init {
        require(value >= 0) { "enqueuedDownloadId must not be negative" }
    }
}
