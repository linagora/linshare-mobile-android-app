package com.linagora.android.linshare.view.sharedspacedocument

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.adapter.sharedspace.action.SharedSpaceNodeDownloadContextMenu
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceChildDocumentsInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSingleSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceDocumentInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnAddButtonClick
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.getParentNodeId
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.operator.download.DownloadOperator
import com.linagora.android.linshare.operator.download.toDownloadRequest
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.Constant.QUERY_INTERVAL_MS
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.action.SearchActionImp
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.sharedspacedocument.action.SharedSpaceDocumentItemBehavior
import com.linagora.android.linshare.view.sharedspacedocument.action.SharedSpaceNodeItemContextMenu
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceDocumentViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getSharedSpaceChildDocumentsInteractor: GetSharedSpaceChildDocumentsInteractor,
    private val getSharedSpaceNodeInteractor: GetSharedSpaceNodeInteractor,
    private val getSingleSharedSpaceInteractor: GetSingleSharedSpaceInteractor,
    private val searchSharedSpaceDocumentInteractor: SearchSharedSpaceDocumentInteractor,
    private val removeSharedSpaceNodeInteractor: RemoveSharedSpaceNodeInteractor,
    private val downloadOperator: DownloadOperator
) : BaseViewModel(dispatcherProvider) {

    companion object {
        val NO_DOWNLOADING_SHARED_SPACE_DOCUMENT = null

        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDocumentViewModel::class.java)
    }

    val listItemBehavior = SharedSpaceDocumentItemBehavior(this)

    val downloadContextMenu = SharedSpaceNodeDownloadContextMenu(this)

    val itemContextMenu = SharedSpaceNodeItemContextMenu(this)

    val navigationPathBehavior = SharedSpaceNavigationPathBehavior(this)

    val searchAction = SearchActionImp(this)

    private val queryChannel = BroadcastChannel<QueryString>(Channel.CONFLATED)

    private val mutableCurrentSharedSpace = MutableLiveData<SharedSpace?>()
    val currentSharedSpace: LiveData<SharedSpace?> = mutableCurrentSharedSpace

    private val mutableCurrentNode = MutableLiveData<WorkGroupNode?>()
    val currentNode: LiveData<WorkGroupNode?> = mutableCurrentNode

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

    fun downloadSharedSpaceDocument(credential: Credential, token: Token, workGroupDocument: WorkGroupDocument) {
        viewModelScope.launch(dispatcherProvider.io) {
            downloadContextMenu.setDownloading(NO_DOWNLOADING_SHARED_SPACE_DOCUMENT)
            downloadOperator.download(credential, token, workGroupDocument.toDownloadRequest())
        }
    }

    fun getDownloading(): WorkGroupNode? {
        return downloadContextMenu.downloadingData.get()
    }

    fun searchDocument(sharedSpaceId: SharedSpaceId, parentNodeId: WorkGroupNodeId?, query: QueryString) {
        viewModelScope.launch(dispatcherProvider.io) {
            queryChannel.send(query)
            consumeStates(queryChannel.asFlow()
                .debounce(QUERY_INTERVAL_MS)
                .flatMapLatest { searchQuery -> getSearchResult(sharedSpaceId, parentNodeId, searchQuery) })
        }
    }

    private fun getSearchResult(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?,
        query: QueryString
    ): Flow<State<Either<Failure, Success>>> {
        return query.takeIf { it.getLength() >= Constant.MIN_LENGTH_CHARACTERS_TO_SEARCH }
            ?.let { searchSharedSpaceDocumentInteractor(sharedSpaceId, parentNodeId, it) }
            ?: getSharedSpaceChildDocumentsInteractor(sharedSpaceId, parentNodeId)
    }

    override fun onSuccessDispatched(success: Success) {
        when (success) {
            is GetSharedSpaceSuccess -> mutableCurrentSharedSpace.value = success.sharedSpace
            is GetSharedSpaceNodeSuccess -> mutableCurrentNode.value = success.node
        }
    }

    fun onAddButtonClick() {
        LOGGER.info("onAddButtonClick()")
        dispatchState(Either.right(SharedSpaceDocumentOnAddButtonClick))
    }

    fun removeSharedSpaceNode(workGroupNode: WorkGroupNode, sharedSpaceId: SharedSpaceId) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(removeSharedSpaceNodeInteractor(sharedSpaceId, workGroupNode.workGroupNodeId))
        }
    }
}
