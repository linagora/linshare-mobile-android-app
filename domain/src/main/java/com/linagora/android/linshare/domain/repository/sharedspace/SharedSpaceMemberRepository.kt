package com.linagora.android.linshare.domain.repository.sharedspace

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember

interface SharedSpaceMemberRepository {

    suspend fun getAllMembers(sharedSpaceId: SharedSpaceId): List<SharedSpaceMember>
}
