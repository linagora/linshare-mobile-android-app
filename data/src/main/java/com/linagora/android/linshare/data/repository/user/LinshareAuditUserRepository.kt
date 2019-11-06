package com.linagora.android.linshare.data.repository.user

import com.linagora.android.linshare.data.datasource.LinshareDataSource
import com.linagora.android.linshare.domain.model.LastLogin
import com.linagora.android.linshare.domain.repository.user.AuditUserRepository
import javax.inject.Inject

class LinshareAuditUserRepository @Inject constructor(
    private val linshareDataSource: LinshareDataSource
) : AuditUserRepository {

    override suspend fun getLastLogin(): LastLogin? {
        return linshareDataSource.getLastLogin()
    }
}
