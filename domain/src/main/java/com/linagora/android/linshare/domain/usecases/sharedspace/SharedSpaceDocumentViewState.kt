package com.linagora.android.linshare.domain.usecases.sharedspace

import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class SharedSpaceDocumentViewState(val documents: List<WorkGroupNode>) : Success.ViewState()
object SharedSpaceDocumentEmpty : Success.ViewState()
data class SharedSpaceDocumentFailure(val throwable: Throwable) : Failure.FeatureFailure()
data class GetSharedSpaceNodeFail(val throwable: Throwable) : Failure.FeatureFailure()
data class GetSharedSpaceNodeSuccess(val node: WorkGroupNode) : Success.ViewState()
data class SharedSpaceDocumentItemClick(val workGroupNode: WorkGroupNode) : Success.ViewEvent()
data class SharedSpaceDocumentContextMenuClick(val workGroupDocument: WorkGroupDocument) : Success.ViewEvent()
object SharedSpaceDocumentOnBackClick : Success.ViewEvent()
data class DownloadSharedSpaceNodeClick(val workGroupNode: WorkGroupNode) : Success.ViewEvent()
object SharedSpaceDocumentOnAddButtonClick : Success.ViewEvent()
object SearchSharedSpaceDocumentNoResult : Failure.FeatureFailure()
data class SearchSharedSpaceDocumentViewState(val documents: List<WorkGroupNode>) : Success.ViewState()
data class RemoveSharedSpaceNodeSuccessViewState(val workGroupNode: WorkGroupNode) : Success.ViewState()
data class RemoveSharedSpaceNodeFailure(val throwable: Throwable) : Failure.FeatureFailure()
data class RemoveSharedSpaceNodeClick(val workGroupNode: WorkGroupNode) : Success.ViewEvent()
object RemoveNodeNotFoundSharedSpaceState : Failure.FeatureFailure()
data class SharedSpaceFolderContextMenuClick(val workGroupFolder: WorkGroupFolder) : Success.ViewEvent()
