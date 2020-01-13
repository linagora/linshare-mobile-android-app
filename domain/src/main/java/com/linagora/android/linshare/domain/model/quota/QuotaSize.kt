package com.linagora.android.linshare.domain.model.quota

data class QuotaSize(val size: Long) {
    init {
        require(size >= 0) { "quota is not negative" }
    }

    operator fun minus(subtrahend: QuotaSize): Long {
        return this.size - subtrahend.size
    }
}
