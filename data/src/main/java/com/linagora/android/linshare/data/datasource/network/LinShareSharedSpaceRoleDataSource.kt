package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.sharedspace.roles.SharedSpaceRoleDataSource
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinShareSharedSpaceRoleDataSource @Inject constructor(
    private val linshareApi: LinshareApi
) : SharedSpaceRoleDataSource {
    override suspend fun findAll(): List<SharedSpaceRole> {
        return linshareApi.getSharedSpaceRoles()
    }
}
