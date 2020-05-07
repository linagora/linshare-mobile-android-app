package com.linagora.android.linshare.view.sharedspacedocument

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceChildDocumentsInteractor
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.getParentNodeId
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedSpaceDocumentViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getSharedSpaceChildDocumentsInteractor: GetSharedSpaceChildDocumentsInteractor
) : BaseViewModel(dispatcherProvider) {

    fun onSwipeRefresh(sharedSpaceNavigationInfo: SharedSpaceNavigationInfo) {
        getAllChildNodes(
            sharedSpaceId = sharedSpaceNavigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            parentNodeId = sharedSpaceNavigationInfo.getParentNodeId()
        )
    }

    fun getAllChildNodes(sharedSpaceId: SharedSpaceId, parentNodeId: WorkGroupNodeId?) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getSharedSpaceChildDocumentsInteractor(sharedSpaceId, parentNodeId))
        }
    }
}
