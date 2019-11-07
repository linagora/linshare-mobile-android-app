package com.linagora.android.linshare.domain.model

import com.linagora.android.linshare.domain.model.quota.QuotaSize

data class AccountQuota(
    val quota: QuotaSize,
    val usedSpace: QuotaSize,
    val maxFileSize: QuotaSize,
    val maintenance: Boolean
)
