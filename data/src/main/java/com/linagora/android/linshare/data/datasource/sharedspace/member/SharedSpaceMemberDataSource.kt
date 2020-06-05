package com.linagora.android.linshare.data.datasource.sharedspace.member

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember

interface SharedSpaceMemberDataSource {

    suspend fun getAllMembers(sharedSpaceId: SharedSpaceId): List<SharedSpaceMember>
}
