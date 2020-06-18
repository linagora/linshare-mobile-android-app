package com.linagora.android.linshare.data.repository.sharedspace

import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.CreateWorkGroupRequest
import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedSpaceRepositoryImp @Inject constructor(
    private val sharedSpaceDataSource: SharedSpaceDataSource
) : SharedSpaceRepository {
    override suspend fun getSharedSpaces(): List<SharedSpaceNodeNested> {
        return sharedSpaceDataSource.getSharedSpaces()
    }

    override suspend fun getSharedSpace(
        sharedSpaceId: SharedSpaceId,
        membersParameter: MembersParameter,
        rolesParameter: RolesParameter
    ): SharedSpace {
        return sharedSpaceDataSource.getSharedSpace(sharedSpaceId, membersParameter, rolesParameter)
    }

    override suspend fun search(query: QueryString): List<SharedSpaceNodeNested> {
        return sharedSpaceDataSource.searchSharedSpaces(query)
    }

    override suspend fun createWorkGroup(createWorkGroupRequest: CreateWorkGroupRequest): SharedSpace {
        return sharedSpaceDataSource.createWorkGroup(createWorkGroupRequest)
    }
}
