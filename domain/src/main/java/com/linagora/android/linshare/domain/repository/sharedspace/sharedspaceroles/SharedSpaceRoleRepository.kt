package com.linagora.android.linshare.domain.repository.sharedspace.sharedspaceroles

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole

interface SharedSpaceRoleRepository {

    suspend fun findAll(): List<SharedSpaceRole>
}
