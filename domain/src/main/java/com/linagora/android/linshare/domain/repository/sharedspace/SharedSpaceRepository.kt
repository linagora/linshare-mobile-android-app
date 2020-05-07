package com.linagora.android.linshare.domain.repository.sharedspace

import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested

interface SharedSpaceRepository {

    suspend fun getSharedSpaces(): List<SharedSpaceNodeNested>

    suspend fun getSharedSpace(
        sharedSpaceId: SharedSpaceId,
        membersParameter: MembersParameter = MembersParameter.WithoutMembers,
        rolesParameter: RolesParameter = RolesParameter.WithRole
    ): SharedSpace

    suspend fun search(query: QueryString): List<SharedSpaceNodeNested>
}