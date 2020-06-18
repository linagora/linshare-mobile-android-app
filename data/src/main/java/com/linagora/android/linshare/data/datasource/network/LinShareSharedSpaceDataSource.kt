package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.data.network.NetworkExecutor
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.CreateWorkGroupRequest
import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.nameContains
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateSharedSpaceException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinShareSharedSpaceDataSource @Inject constructor(
    private val linshareApi: LinshareApi,
    private val networkExecutor: NetworkExecutor
) : SharedSpaceDataSource {

    override suspend fun getSharedSpaces(): List<SharedSpaceNodeNested> {
        return linshareApi.getSharedSpaces().sortedByDescending { it.modificationDate }
    }

    override suspend fun getSharedSpace(
        sharedSpaceId: SharedSpaceId,
        membersParameter: MembersParameter,
        rolesParameter: RolesParameter
    ): SharedSpace {
        return linshareApi.getSharedSpace(
            sharedSpaceId.uuid.toString(),
            membersParameter.takeIf { it == MembersParameter.WithMembers }
                ?.let { true }
                ?: false,
            rolesParameter.takeIf { it == RolesParameter.WithRole }
                ?.let { true }
                ?: false
        )
    }
    override suspend fun searchSharedSpaces(query: QueryString): List<SharedSpaceNodeNested> {
        return getSharedSpaces()
            .filter { shareSpaceNodeNested -> shareSpaceNodeNested.nameContains(query.value) }
    }

    override suspend fun createWorkGroup(createWorkGroupRequest: CreateWorkGroupRequest): SharedSpace {
        return networkExecutor.execute(
            networkRequest = { linshareApi.createWorkGroup(createWorkGroupRequest) },
            onFailure = { throw CreateSharedSpaceException(it) })
    }
}
