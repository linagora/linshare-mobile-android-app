package com.linagora.android.linshare.domain.usecases.sharedspace.member

import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class GetMembersFailed(val throwable: Throwable) : Failure.FeatureFailure()
object GetMembersNoResult : Failure.FeatureFailure()
data class GetMembersSuccess(val members: List<SharedSpaceMember>) : Success.ViewState()
data class AddMemberSuccess(val member: SharedSpaceMember) : Success.ViewState()
data class AddMemberFailed(val throwable: Throwable) : Failure.FeatureFailure()
