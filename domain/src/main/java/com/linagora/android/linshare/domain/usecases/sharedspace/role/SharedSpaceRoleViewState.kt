package com.linagora.android.linshare.domain.usecases.sharedspace.role

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class GetAllSharedSpaceRolesSuccess(val roles: List<SharedSpaceRole>) : Success.ViewState()
data class GetAllSharedSpaceRolesFailed(val throwable: Throwable) : Failure.FeatureFailure()
