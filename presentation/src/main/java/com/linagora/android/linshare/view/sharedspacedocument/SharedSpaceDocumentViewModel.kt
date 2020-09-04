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

package com.linagora.android.linshare.view.sharedspacedocument

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.flatMap
import com.linagora.android.linshare.adapter.sharedspace.action.SharedSpaceNodeDownloadContextMenu
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.order.OrderListType
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.CreateSharedSpaceNodeRequest
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeType
import com.linagora.android.linshare.domain.model.sharedspace.createCopyRequest
import com.linagora.android.linshare.domain.model.sharedspace.toCopyToMySpaceRequest
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest
import com.linagora.android.linshare.domain.usecases.copy.CopyInMySpaceInteractor
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationInteractor
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationSuccess
import com.linagora.android.linshare.domain.usecases.order.PersistOrderListConfigurationInteractor
import com.linagora.android.linshare.domain.usecases.order.PersistOrderListConfigurationSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.CopyToSharedSpace
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateSharedSpaceNodeInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.DuplicatedNameError
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceChildDocumentsOrderedInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSingleSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.NotDuplicatedName
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceDocumentInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnAddButtonClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.getCurrentNodeId
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.operator.download.DownloadOperator
import com.linagora.android.linshare.operator.download.toDownloadRequest
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.Constant.QUERY_INTERVAL_MS
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.NameValidator
import com.linagora.android.linshare.view.action.OrderByActionImp
import com.linagora.android.linshare.view.action.SearchActionImp
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.sharedspacedocument.action.CreateFolderBehavior
import com.linagora.android.linshare.view.sharedspacedocument.action.OnSelectedSharedSpaceDocumentAddBehaviorImpl
import com.linagora.android.linshare.view.sharedspacedocument.action.SharedSpaceDocumentCopyToContextMenu
import com.linagora.android.linshare.view.sharedspacedocument.action.SharedSpaceDocumentItemBehavior
import com.linagora.android.linshare.view.sharedspacedocument.action.SharedSpaceNodeItemContextMenu
import com.linagora.android.linshare.view.sharedspacedocument.action.SharedSpaceSelectDestinationSpaceTypeAction
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceDocumentViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getSharedSpaceChildDocumentsOrderedInteractor: GetSharedSpaceChildDocumentsOrderedInteractor,
    private val getSharedSpaceNodeInteractor: GetSharedSpaceNodeInteractor,
    private val getSingleSharedSpaceInteractor: GetSingleSharedSpaceInteractor,
    private val searchSharedSpaceDocumentInteractor: SearchSharedSpaceDocumentInteractor,
    private val removeSharedSpaceNodeInteractor: RemoveSharedSpaceNodeInteractor,
    private val getOrderListConfigurationInteractor: GetOrderListConfigurationInteractor,
    private val persistOrderListConfigurationInteractor: PersistOrderListConfigurationInteractor,
    private val downloadOperator: DownloadOperator,
    private val copyToSharedSpace: CopyToSharedSpace,
    private val copyToMySpace: CopyInMySpaceInteractor,
    private val nameValidator: NameValidator,
    private val createSharedSpaceNodeInteractor: CreateSharedSpaceNodeInteractor
) : BaseViewModel(internetAvailable, dispatcherProvider) {

    companion object {
        val NO_DOWNLOADING_SHARED_SPACE_DOCUMENT = null

        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDocumentViewModel::class.java)
    }

    val listItemBehavior = SharedSpaceDocumentItemBehavior(this)

    val downloadContextMenu = SharedSpaceNodeDownloadContextMenu(this)

    val itemContextMenu = SharedSpaceNodeItemContextMenu(this)

    val navigationPathBehavior = SharedSpaceNavigationPathBehavior(this)

    val searchAction = SearchActionImp(this)

    val orderByAction = OrderByActionImp(this)

    val copyToContextMenu = SharedSpaceDocumentCopyToContextMenu(this)

    val selectDestinationSpaceTypeAction = SharedSpaceSelectDestinationSpaceTypeAction(this)

    val selectedBehavior = OnSelectedSharedSpaceDocumentAddBehaviorImpl(this)

    val createFolderBehavior = CreateFolderBehavior(this)

    private val queryChannel = BroadcastChannel<QueryString>(Channel.CONFLATED)

    private val mutableCurrentSharedSpace = MutableLiveData<SharedSpace?>()
    val currentSharedSpace: LiveData<SharedSpace?> = mutableCurrentSharedSpace

    private val mutableCurrentNode = MutableLiveData<WorkGroupNode?>()
    val currentNode: LiveData<WorkGroupNode?> = mutableCurrentNode

    private val mutableListWorkGroupNode = MutableLiveData<List<WorkGroupNode>>(emptyList())
    val listWorkGroupNode: LiveData<List<WorkGroupNode>> = mutableListWorkGroupNode

    private val enteringName = BroadcastChannel<NewNameRequest>(Channel.CONFLATED)

    private val newNameStates = enteringName.asFlow()
        .debounce(QUERY_INTERVAL_MS)
        .mapLatest { queryString -> checkDuplicateName(queryString) }
        .mapLatest { duplicatedState -> validateName(duplicatedState) }

    fun onSwipeRefresh(sharedSpaceNavigationInfo: SharedSpaceNavigationInfo) {
        getAllChildNodes(
            sharedSpaceId = sharedSpaceNavigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            parentNodeId = sharedSpaceNavigationInfo.getCurrentNodeId()
        )
    }

    private fun checkDuplicateName(queryString: NewNameRequest): Either<Failure, Success> {
        return Either.cond(
            test = listWorkGroupNode.value?.map { it.name }?.contains(queryString.value) == false,
            ifTrue = { NotDuplicatedName(queryString) },
            ifFalse = { DuplicatedNameError }
        )
    }

    private fun validateName(duplicatedState: Either<Failure, Success>): Either<Failure, Success> {
        return duplicatedState.flatMap { state ->
            if (state is NotDuplicatedName) {
                return@flatMap nameValidator.validateName(state.verifiedName.value)
            }
            return@flatMap Either.right(state)
        }
    }

    fun verifyNewName(nameString: NewNameRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            enteringName.send(nameString)
            consumeStates(
                newNameStates.flatMapLatest { state ->
                    flow<State<Either<Failure, Success>>> { emitState { state } } }
            )
        }
    }

    fun getAllChildNodes(sharedSpaceId: SharedSpaceId, parentNodeId: WorkGroupNodeId?) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getSharedSpaceChildDocumentsOrderedInteractor(sharedSpaceId, parentNodeId, orderByAction.getCurrentOrderListConfigurationType()))
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
            ?: getSharedSpaceChildDocumentsOrderedInteractor(sharedSpaceId, parentNodeId, orderByAction.getCurrentOrderListConfigurationType())
    }

    fun getOrderListConfiguration() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getOrderListConfigurationInteractor(OrderListType.SharedSpaceDocument))
        }
    }

    fun persistOrderListConfiguration(orderListConfigurationType: OrderListConfigurationType) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(persistOrderListConfigurationInteractor(OrderListType.SharedSpaceDocument, orderListConfigurationType))
        }
    }

    override fun onSuccessDispatched(success: Success) {
        when (success) {
            is GetSharedSpaceSuccess -> mutableCurrentSharedSpace.value = success.sharedSpace
            is GetSharedSpaceNodeSuccess -> mutableCurrentNode.value = success.node
            is SharedSpaceDocumentViewState -> mutableListWorkGroupNode.value = success.documents
            is GetOrderListConfigurationSuccess -> orderByAction.setCurrentOrderListConfigurationType(success.orderListConfigurationType)
            is PersistOrderListConfigurationSuccess -> getOrderListConfiguration()
        }
    }

    fun onAddButtonClick() {
        LOGGER.info("onAddButtonClick()")
        dispatchState(Either.right(SharedSpaceDocumentOnAddButtonClick))
    }

    fun removeSharedSpaceNode(sharedSpaceId: SharedSpaceId, workGroupNode: WorkGroupNode) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(removeSharedSpaceNodeInteractor(sharedSpaceId, workGroupNode.workGroupNodeId))
        }
    }

    fun copyNodeToSharedSpace(
        copyFromNodeId: WorkGroupNodeId,
        copyToSharedSpaceId: SharedSpaceId,
        copyToParentNodeId: WorkGroupNodeId
    ) {
        LOGGER.info("copyNodeToSharedSpace(): copy $copyFromNodeId to $copyToParentNodeId in $copyToSharedSpaceId")
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(copyToSharedSpace(
                copyFromNodeId.createCopyRequest(),
                copyToSharedSpaceId,
                copyToParentNodeId
            ))
        }
    }

    fun copyNodeToMySpace(copyFromNode: WorkGroupNode) {
        LOGGER.info("copyNodeToMySpace(): $copyFromNode")
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(copyToMySpace(copyFromNode.toCopyToMySpaceRequest()))
        }
    }

    fun createFolder(nameFolder: NewNameRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(OperatorType.CreateFolder) {
                currentNode.value?.let {
                    createSharedSpaceNodeInteractor(
                        it.sharedSpaceId,
                        CreateSharedSpaceNodeRequest(
                            nameFolder.value,
                            it.workGroupNodeId,
                            WorkGroupNodeType.FOLDER)
                    )
                }!!
            }
        }
    }
}
