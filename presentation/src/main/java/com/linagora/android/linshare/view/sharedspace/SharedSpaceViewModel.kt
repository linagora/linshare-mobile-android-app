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
import com.linagora.android.linshare.util.ConnectionLiveData
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
    override val internetAvailable: ConnectionLiveData,
    private val searchSharedSpaceInteractor: SearchSharedSpaceInteractor,
    private val getSharedSpaceInteractor: GetSharedSpaceInteractor,
    private val createWorkGroupInteractor: CreateWorkGroupInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val nameValidator: NameValidator
) : BaseViewModel(internetAvailable, dispatcherProvider) {

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
