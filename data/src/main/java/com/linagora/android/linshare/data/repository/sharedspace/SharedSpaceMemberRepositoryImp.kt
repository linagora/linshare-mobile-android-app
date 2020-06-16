package com.linagora.android.linshare.data.repository.sharedspace

import com.linagora.android.linshare.data.datasource.sharedspace.member.SharedSpaceMemberDataSource
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.member.AddMemberRequest
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceMemberRepository
import javax.inject.Inject

class SharedSpaceMemberRepositoryImp @Inject constructor(
    private val linShareSharedSpaceMemberDataSource: SharedSpaceMemberDataSource
) : SharedSpaceMemberRepository {

    override suspend fun getAllMembers(sharedSpaceId: SharedSpaceId): List<SharedSpaceMember> {
        return linShareSharedSpaceMemberDataSource.getAllMembers(sharedSpaceId)
    }

    override suspend fun addMember(
        addMemberRequest: AddMemberRequest
    ): SharedSpaceMember {
        return linShareSharedSpaceMemberDataSource.addMember(addMemberRequest)
    }
}
