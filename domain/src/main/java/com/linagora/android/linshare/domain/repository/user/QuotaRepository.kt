package com.linagora.android.linshare.domain.repository.user

import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.quota.QuotaId

interface QuotaRepository {

    suspend fun findQuota(quotaUuid: QuotaId): AccountQuota?
}
