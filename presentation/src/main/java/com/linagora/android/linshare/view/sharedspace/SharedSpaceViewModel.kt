package com.linagora.android.linshare.view.sharedspace

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.Constant.MIN_LENGTH_CHARACTERS_TO_SEARCH
import com.linagora.android.linshare.util.Constant.QUERY_INTERVAL_MS
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.action.SearchActionImp
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.sharedspace.action.SharedSpaceItemBehavior
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceViewModel @Inject constructor(
    private val searchSharedSpaceInteractor: SearchSharedSpaceInteractor,
    private val getSharedSpaceInteractor: GetSharedSpaceInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    val sharedSpaceItemBehavior = SharedSpaceItemBehavior(this)

    val searchAction = SearchActionImp(this)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceViewModel::class.java)
    }

    private val queryChannel = BroadcastChannel<QueryString>(Channel.CONFLATED)

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
}
