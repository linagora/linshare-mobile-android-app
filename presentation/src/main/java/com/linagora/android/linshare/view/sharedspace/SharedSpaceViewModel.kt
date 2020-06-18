package com.linagora.android.linshare.view.sharedspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.CreateWorkGroupRequest
import com.linagora.android.linshare.domain.model.sharedspace.LinShareNodeType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateWorkGroupButtonBottomBarClick
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateWorkGroupInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.linshare.util.Constant.MIN_LENGTH_CHARACTERS_TO_SEARCH
import com.linagora.android.linshare.util.Constant.QUERY_INTERVAL_MS
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.NameValidator
import com.linagora.android.linshare.view.action.SearchActionImp
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.sharedspace.action.CreateWorkGroupBehavior
import com.linagora.android.linshare.view.sharedspace.action.SharedSpaceItemBehavior
import com.linagora.android.linshare.view.sharedspace.action.SharedSpaceItemContextMenu
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedSpaceViewModel @Inject constructor(
    private val searchSharedSpaceInteractor: SearchSharedSpaceInteractor,
    private val getSharedSpaceInteractor: GetSharedSpaceInteractor,
    private val createWorkGroupInteractor: CreateWorkGroupInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val nameValidator: NameValidator
) : BaseViewModel(dispatcherProvider) {

    val sharedSpaceItemBehavior = SharedSpaceItemBehavior(this)

    val searchAction = SearchActionImp(this)

    val sharedSpaceItemContextMenu = SharedSpaceItemContextMenu(this)

    private val mutableListSharedSpaceNodeNested = MutableLiveData<List<SharedSpaceNodeNested>>()
    val listSharedSpaceNodeNested: LiveData<List<SharedSpaceNodeNested>> = mutableListSharedSpaceNodeNested

    val createWorkGroupBehavior = CreateWorkGroupBehavior(this)

    private val queryChannel = BroadcastChannel<QueryString>(Channel.CONFLATED)

    private val enterTextNameWorkGroup = BroadcastChannel<NewNameRequest>(Channel.CONFLATED)

    private val nameEnter = enterTextNameWorkGroup.asFlow()
        .debounce(QUERY_INTERVAL_MS)
        .mapLatest { queryString -> nameValidator.validateName(queryString.value) }

    fun onSwipeRefresh() {
        getSharedSpace()
    }

    fun searchWithQuery(query: QueryString) {
        viewModelScope.launch(dispatcherProvider.io) {
            queryChannel.send(query)
            consumeStates(queryChannel.asFlow()
                .debounce(QUERY_INTERVAL_MS)
                .flatMapLatest { searchQuery -> getSearchResult(searchQuery) }
            )
        }
    }

    fun validName(nameString: NewNameRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            enterTextNameWorkGroup.send(nameString)
            consumeStates(nameEnter.flatMapLatest { state -> flow<State<Either<Failure, Success>>> { emitState { state } } })
        }
    }

    private fun getSearchResult(query: QueryString): Flow<State<Either<Failure, Success>>> {
        return query.takeIf { it.getLength() >= MIN_LENGTH_CHARACTERS_TO_SEARCH }
            ?.let { searchSharedSpaceInteractor(it) }
            ?: getSharedSpaceInteractor()
    }

    fun getSharedSpace() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getSharedSpaceInteractor())
        }
    }

    fun onUploadBottomBarClick() {
        dispatchState(Either.right(CreateWorkGroupButtonBottomBarClick))
    }

    fun createWorkGroup(nameWorkGroup: NewNameRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(createWorkGroupInteractor(CreateWorkGroupRequest(nameWorkGroup.value, LinShareNodeType.WORK_GROUP)))
        }
    }

    override fun onSuccessDispatched(success: Success) {
        when (success) {
            is SharedSpaceViewState -> mutableListSharedSpaceNodeNested.value = success.sharedSpace
        }
    }
}
