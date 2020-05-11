package com.linagora.android.linshare.data.datasource

import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested

interface SharedSpaceDataSource {

    suspend fun getSharedSpaces(): List<SharedSpaceNodeNested>

    suspend fun getSharedSpace(
        sharedSpaceId: SharedSpaceId,
        membersParameter: MembersParameter = MembersParameter.WithoutMembers,
        rolesParameter: RolesParameter = RolesParameter.WithRole
    ): SharedSpace
}
