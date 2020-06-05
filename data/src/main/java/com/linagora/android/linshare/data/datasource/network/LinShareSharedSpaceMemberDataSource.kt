package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.sharedspace.member.SharedSpaceMemberDataSource
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinShareSharedSpaceMemberDataSource @Inject constructor(
    private val linShareApi: LinshareApi
) : SharedSpaceMemberDataSource {

    override suspend fun getAllMembers(sharedSpaceId: SharedSpaceId): List<SharedSpaceMember> {
        return linShareApi.getMembers(sharedSpaceId.uuid.toString())
    }
}
