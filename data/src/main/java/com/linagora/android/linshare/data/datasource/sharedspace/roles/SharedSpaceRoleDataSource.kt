package com.linagora.android.linshare.data.datasource.sharedspace.roles

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole

interface SharedSpaceRoleDataSource {
    suspend fun findAll(): List<SharedSpaceRole>
}
