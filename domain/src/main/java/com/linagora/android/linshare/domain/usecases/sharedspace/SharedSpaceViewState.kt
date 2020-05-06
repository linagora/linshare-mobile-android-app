package com.linagora.android.linshare.domain.usecases.sharedspace

import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class SharedSpaceViewState(val sharedSpace: List<ShareSpaceNodeNested>) : Success.ViewState()
data class SharedSpaceFailure(val throwable: Throwable) : FeatureFailure()
object EmptySharedSpaceState : Success.ViewState()
