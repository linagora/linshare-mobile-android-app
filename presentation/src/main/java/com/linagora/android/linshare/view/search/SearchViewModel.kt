package com.linagora.android.linshare.view.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.domain.usecases.remove.RemoveDocumentInteractor
import com.linagora.android.linshare.domain.usecases.search.SearchInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.operator.download.DownloadOperator
import com.linagora.android.linshare.operator.download.toDownloadRequest
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.action.MySpaceItemActionImp
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ListItemBehavior
import com.linagora.android.linshare.view.myspace.MySpaceViewModel
import com.linagora.android.linshare.view.myspace.action.MySpaceDownloadContextMenu
import com.linagora.android.linshare.view.myspace.action.MySpaceItemContextMenu
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val searchInteractor: SearchInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val downloadOperator: DownloadOperator,
    private val removeDocumentInteractor: RemoveDocumentInteractor
) : BaseViewModel(internetAvailable, dispatcherProvider),
    ListItemBehavior<Document> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchViewModel::class.java)
        const val QUERY_INTERVAL_MS = 500L
    }

    val mySpaceItemAction = MySpaceItemActionImp(this)

    val itemContextMenu = MySpaceItemContextMenu(this)

    val downloadContextMenu = MySpaceDownloadContextMenu(this)

    private val searchState = MutableLiveData<Either<Failure, Success>>()
        .apply { value = INITIAL_STATE }

    val queryChannel = BroadcastChannel<QueryString>(Channel.CONFLATED)

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

    override fun onContextMenuClick(data: Document) {
        LOGGER.info("onContextMenuClick() $data")
        dispatchState(Either.right(ContextMenuClick(data)))
    }

    fun removeDocument(document: Document) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(removeDocumentInteractor(document.documentId))
        }
    }

    fun getDownloadingDocument(): Document? {
        return downloadContextMenu.downloadingData.get()
    }

    fun downloadDocument(credential: Credential, token: Token, document: Document) {
        viewModelScope.launch(dispatcherProvider.io) {
            downloadContextMenu.setDownloading(MySpaceViewModel.NO_DOWNLOADING_DOCUMENT)
            downloadOperator.download(credential, token, document.toDownloadRequest())
        }
    }
}
