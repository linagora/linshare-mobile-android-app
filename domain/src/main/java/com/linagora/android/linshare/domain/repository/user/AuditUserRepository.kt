package com.linagora.android.linshare.domain.repository.user

import com.linagora.android.linshare.domain.model.LastLogin

interface AuditUserRepository {

    suspend fun getLastLogin(): LastLogin?
}
