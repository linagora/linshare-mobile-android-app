package com.linagora.android.linshare.view.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.search.SearchInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ListItemBehavior
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchInteractor: SearchInteractor,
    dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider), ListItemBehavior<Document> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchViewModel::class.java)
        const val QUERY_INTERVAL_MS = 500L
    }

    private val searchState = MutableLiveData<Either<Failure, Success>>()
        .apply { value = INITIAL_STATE }

    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    private val resultState = queryChannel.asFlow()
        .debounce(QUERY_INTERVAL_MS)
        .flatMapLatest { searchInteractor(it) }
        .map { dispatchSearchState(it) }

    val searchResult = resultState.asLiveData()

    private fun dispatchSearchState(state: State<Either<Failure, Success>>): Either<Failure, Success> {
        val newState = state(searchState.value!!)
        searchState.postValue(newState)
        return newState
    }

    override fun onContextMenuClick(document: Document) {
        LOGGER.info("onContextMenuClick() $document")
    }
}
