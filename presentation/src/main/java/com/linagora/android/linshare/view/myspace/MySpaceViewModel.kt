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

package com.linagora.android.linshare.view.myspace

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.toCopyRequest
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.order.OrderListType
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest
import com.linagora.android.linshare.domain.usecases.copy.CopyInMySpaceInteractor
import com.linagora.android.linshare.domain.usecases.myspace.GetAllDocumentsOrderedInteractor
import com.linagora.android.linshare.domain.usecases.myspace.UploadButtonBottomBarClick
import com.linagora.android.linshare.domain.usecases.myspace.RenameDocumentInteractor
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationInteractor
import com.linagora.android.linshare.domain.usecases.order.PersistOrderListConfigurationInteractor
import com.linagora.android.linshare.domain.usecases.order.PersistOrderListConfigurationSuccess
import com.linagora.android.linshare.domain.usecases.remove.RemoveDocumentInteractor
import com.linagora.android.linshare.domain.usecases.search.SearchInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.CopyToSharedSpace
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.functionality.FunctionalityObserver
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.linshare.operator.download.DownloadOperator
import com.linagora.android.linshare.operator.download.toDownloadRequest
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.NameValidator
import com.linagora.android.linshare.view.LinShareApplication
import com.linagora.android.linshare.view.action.MySpaceItemActionImp
import com.linagora.android.linshare.view.action.OrderByActionImp
import com.linagora.android.linshare.view.action.SearchActionImp
import com.linagora.android.linshare.view.base.LinShareViewModel
import com.linagora.android.linshare.view.myspace.action.MySpaceCopyToContextMenu
import com.linagora.android.linshare.view.myspace.action.MySpaceDownloadContextMenu
import com.linagora.android.linshare.view.myspace.action.MySpaceEditContextMenu
import com.linagora.android.linshare.view.myspace.action.MySpaceItemBehavior
import com.linagora.android.linshare.view.myspace.action.MySpaceItemContextMenu
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

class MySpaceViewModel @Inject constructor(
    application: LinShareApplication,
    override val internetAvailable: ConnectionLiveData,
    private val getAllDocumentsOrderedInteractor: GetAllDocumentsOrderedInteractor,
    private val getOrderListConfigurationInteractor: GetOrderListConfigurationInteractor,
    private val persistOrderListConfigurationInteractor: PersistOrderListConfigurationInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val downloadOperator: DownloadOperator,
    private val removeDocumentInteractor: RemoveDocumentInteractor,
    private val copyInMySpaceInteractor: CopyInMySpaceInteractor,
    private val searchInteractor: SearchInteractor,
    private val copyToSharedSpace: CopyToSharedSpace,
    val functionalityObserver: FunctionalityObserver,
    private val renameDocumentInteractor: RenameDocumentInteractor,
    private val nameValidator: NameValidator
) : LinShareViewModel(internetAvailable, application, dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MySpaceViewModel::class.java)

        val NO_DOWNLOADING_DOCUMENT = null
    }

    val mySpaceItemBehavior = MySpaceItemBehavior(this)

    val mySpaceItemAction = MySpaceItemActionImp(this)

    val orderByAction = OrderByActionImp(this)

    val itemContextMenu = MySpaceItemContextMenu(this)

    val downloadContextMenu = MySpaceDownloadContextMenu(this)

    val copyToSharedSpaceContextMenu = MySpaceCopyToContextMenu(this)

    val editContextMenu = MySpaceEditContextMenu(this)

    val searchAction = SearchActionImp(this)

    private val queryChannel = BroadcastChannel<QueryString>(Channel.CONFLATED)

    private val enteringName = BroadcastChannel<NewNameRequest>(Channel.CONFLATED)

    private val newNameStates = enteringName.asFlow()
        .debounce(Constant.QUERY_INTERVAL_MS)
        .mapLatest { queryString -> nameValidator.validateName(queryString.value) }

    fun getOrderListConfiguration() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getOrderListConfigurationInteractor(OrderListType.MySpace))
        }
    }

    override fun onSuccessDispatched(success: Success) {
        when (success) {
            is PersistOrderListConfigurationSuccess -> getOrderListConfiguration()
        }
    }

    fun setCurrentOrderListConfigurationType(orderListConfigurationType: OrderListConfigurationType) {
        orderByAction.setCurrentOrderListConfigurationType(orderListConfigurationType)
    }

    fun persistOrderListConfiguration(orderListConfigurationType: OrderListConfigurationType) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(persistOrderListConfigurationInteractor(OrderListType.MySpace, orderListConfigurationType))
        }
    }

    fun getAllDocuments() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAllDocumentsOrderedInteractor(orderByAction.getCurrentOrderListConfigurationType()))
        }
    }

    fun onSwipeRefresh() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(OperatorType.SwiftRefresh) { getAllDocumentsOrderedInteractor(orderByAction.getCurrentOrderListConfigurationType()) }
        }
    }

    fun onUploadBottomBarClick() {
        LOGGER.info("onUploadBottomBarClick()")
        dispatchState(Either.right(UploadButtonBottomBarClick))
    }

    fun getDownloadingDocument(): Document? {
        return downloadContextMenu.downloadingData.get()
    }

    fun removeDocument(document: Document) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(removeDocumentInteractor(document.documentId))
        }
    }

    fun downloadDocument(credential: Credential, token: Token, document: Document) {
        viewModelScope.launch(dispatcherProvider.io) {
            downloadContextMenu.setDownloading(NO_DOWNLOADING_DOCUMENT)
            downloadOperator.download(credential, token, document.toDownloadRequest())
        }
    }

    fun copyDocumentToSharedSpace(
        document: Document,
        copyToSharedSpaceId: SharedSpaceId,
        copyToParentNodeId: WorkGroupNodeId
    ) {
        LOGGER.info("copyDocumentToSharedSpace(): copy $document to $copyToParentNodeId in $copyToSharedSpaceId")
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(copyToSharedSpace(
                document.toCopyRequest(),
                copyToSharedSpaceId,
                copyToParentNodeId
            ))
        }
    }

    fun duplicateDocumentMySpace(
        document: Document
    ) {
        LOGGER.info("duplicateDocumentMySpace(): duplicate $document in My Space")
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(copyInMySpaceInteractor(document.toCopyRequest()))
        }
    }

    fun renameDocument(document: Document, newNameRequest: NewNameRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(renameDocumentInteractor(document, newNameRequest))
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

    fun searchDocument(query: QueryString) {
        viewModelScope.launch(dispatcherProvider.io) {
            queryChannel.send(query)
            consumeStates(queryChannel.asFlow()
                .debounce(Constant.QUERY_INTERVAL_MS)
                .flatMapLatest { searchQuery -> getSearchResult(searchQuery) })
        }
    }

    private fun getSearchResult(query: QueryString): Flow<State<Either<Failure, Success>>> {
        return query.takeIf { it.getLength() >= Constant.MIN_LENGTH_CHARACTERS_TO_SEARCH }
            ?.let { searchInteractor(query, orderByAction.getCurrentOrderListConfigurationType()) }
            ?: getAllDocumentsOrderedInteractor(orderByAction.getCurrentOrderListConfigurationType())
    }
}
