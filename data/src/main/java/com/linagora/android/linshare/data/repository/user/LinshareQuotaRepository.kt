package com.linagora.android.linshare.data.repository.user

import com.linagora.android.linshare.data.datasource.LinshareDataSource
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import javax.inject.Inject

class LinshareQuotaRepository @Inject constructor(
    private val linshareDataSource: LinshareDataSource
) : QuotaRepository {

    override suspend fun findQuota(quotaUuid: String): AccountQuota? {
        return linshareDataSource.findQuota(quotaUuid)
    }
}
