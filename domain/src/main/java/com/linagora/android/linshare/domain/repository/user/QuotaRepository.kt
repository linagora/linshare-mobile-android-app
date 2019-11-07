package com.linagora.android.linshare.domain.repository.user

import com.linagora.android.linshare.domain.model.AccountQuota

interface QuotaRepository {

    suspend fun findQuota(quotaUuid: String): AccountQuota?
}
