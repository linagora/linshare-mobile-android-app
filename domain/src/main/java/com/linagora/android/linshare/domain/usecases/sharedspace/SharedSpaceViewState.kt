package com.linagora.android.linshare.domain.usecases.sharedspace

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class SharedSpaceViewState(val sharedSpace: List<SharedSpaceNodeNested>) : Success.ViewState()
data class SharedSpaceFailure(val throwable: Throwable) : FeatureFailure()
data class SearchSharedSpaceViewState(val sharedSpace: List<SharedSpaceNodeNested>) : Success.ViewState()
object EmptySharedSpaceState : Failure.FeatureFailure()
data class SharedSpaceItemClick(val sharedSpaceNodeNested: SharedSpaceNodeNested) : Success.ViewEvent()
data class SharedSpaceContextMenuClick(val sharedSpaceNodeNested: SharedSpaceNodeNested) : Success.ViewEvent()
data class GetSharedSpaceSuccess(val sharedSpace: SharedSpace) : Success.ViewState()
data class GetSharedSpaceFailed(val throwable: Throwable) : FeatureFailure()
object NoResultsSearchSharedSpace : Failure.FeatureFailure()
object SearchSharedSpaceInitial : Success.ViewState()
data class DetailsSharedSpaceItem(val sharedSpaceNodeNested: SharedSpaceNodeNested) : Success.ViewEvent()
data class OpenAddMembers(val sharedSpaceId: SharedSpaceId) : Success.ViewEvent()
data class CreateWorkGroupSuccess(val sharedSpace: SharedSpace) : Success.ViewState()
data class CreateWorkGroupFailed(val throwable: Throwable) : FeatureFailure()
object CreateWorkGroupButtonBottomBarClick : Success.ViewEvent()
object CancelCreateWorkGroupViewState : Success.ViewEvent()
data class CreateWorkGroupViewState(val nameWorkGroup: NewNameRequest) : Success.ViewEvent()
object BlankNameError : Failure.FeatureFailure()
object NameContainSpecialCharacter : Failure.FeatureFailure()
data class ValidName(val nameWorkGroup: String) : Success.ViewState()
