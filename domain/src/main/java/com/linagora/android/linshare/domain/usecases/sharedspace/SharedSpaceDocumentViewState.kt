package com.linagora.android.linshare.domain.usecases.sharedspace

import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class SharedSpaceDocumentViewState(val documents: List<WorkGroupNode>) : Success.ViewState()
object SharedSpaceDocumentEmpty : Success.ViewState()
data class SharedSpaceDocumentFailure(val throwable: Throwable) : Failure.FeatureFailure()
data class GetSharedSpaceNodeFail(val throwable: Throwable) : Failure.FeatureFailure()
data class GetSharedSpaceNodeSuccess(val node: WorkGroupNode) : Success.ViewState()
