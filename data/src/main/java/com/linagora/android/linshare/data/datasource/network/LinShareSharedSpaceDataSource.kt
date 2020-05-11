package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinShareSharedSpaceDataSource @Inject constructor(
    private val linshareApi: LinshareApi
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
}
