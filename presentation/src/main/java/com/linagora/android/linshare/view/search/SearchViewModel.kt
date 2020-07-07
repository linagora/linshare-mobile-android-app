/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

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
