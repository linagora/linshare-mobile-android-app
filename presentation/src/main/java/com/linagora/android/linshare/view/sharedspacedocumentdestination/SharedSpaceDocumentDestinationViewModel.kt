package com.linagora.android.linshare.view.sharedspacedocumentdestination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceChildDocumentsInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSingleSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.getParentNodeId
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.sharedspacedocument.action.SharedSpaceDocumentItemBehavior
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceDocumentDestinationViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getSharedSpaceChildDocumentsInteractor: GetSharedSpaceChildDocumentsInteractor,
    private val getSharedSpaceNodeInteractor: GetSharedSpaceNodeInteractor,
    private val getSingleSharedSpaceInteractor: GetSingleSharedSpaceInteractor
) : BaseViewModel(dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDocumentDestinationViewModel::class.java)
    }

    private val mutableCurrentSharedSpace = MutableLiveData<SharedSpace?>()
    val currentSharedSpace: LiveData<SharedSpace?> = mutableCurrentSharedSpace

    private val mutableCurrentNode = MutableLiveData<WorkGroupNode?>()
    val currentNode: LiveData<WorkGroupNode?> = mutableCurrentNode

    val listItemBehavior = SharedSpaceDocumentItemBehavior(this)

    val pickDestinationBehavior = SelectDestinationBehavior(this)

    fun onSwipeRefresh(sharedSpaceNavigationInfo: SharedSpaceNavigationInfo) {
        getAllChildNodes(
            sharedSpaceId = sharedSpaceNavigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            parentNodeId = sharedSpaceNavigationInfo.getParentNodeId()
        )
    }

    fun getAllChildNodes(sharedSpaceId: SharedSpaceId, parentNodeId: WorkGroupNodeId?) {
        LOGGER.info("getAllChildNodes() $sharedSpaceId $parentNodeId ")
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getSharedSpaceChildDocumentsInteractor(sharedSpaceId, parentNodeId))
        }
    }

    fun getCurrentNode(sharedSpaceId: SharedSpaceId, currentNodeId: WorkGroupNodeId) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getSharedSpaceNodeInteractor(sharedSpaceId, currentNodeId))
        }
    }

    fun getCurrentSharedSpace(sharedSpaceId: SharedSpaceId) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getSingleSharedSpaceInteractor(sharedSpaceId))
        }
    }

    override fun onSuccessDispatched(success: Success) {
        when (success) {
            is GetSharedSpaceSuccess -> mutableCurrentSharedSpace.value = success.sharedSpace
            is GetSharedSpaceNodeSuccess -> mutableCurrentNode.value = success.node
        }
    }
}
