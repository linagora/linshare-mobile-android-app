package com.linagora.android.linshare.data.repository.sharedspace

import com.linagora.android.linshare.data.datasource.sharedspace.roles.SharedSpaceRoleDataSource
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.repository.sharedspace.sharedspaceroles.SharedSpaceRoleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedSpaceRoleRepositoryImp @Inject constructor(
    private val sharedSpaceRoleDataSource: SharedSpaceRoleDataSource
) : SharedSpaceRoleRepository {
    override suspend fun findAll(): List<SharedSpaceRole> {
        return sharedSpaceRoleDataSource.findAll()
    }
}
