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

package com.linagora.android.linshare.view.receivedshares

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.adapter.receivedshares.action.ReceivedShareDownloadContextMenu
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.copy.SpaceType
import com.linagora.android.linshare.domain.model.copy.toCopyRequest
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.order.OrderListType
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.copy.CopyInMySpaceInteractor
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationInteractor
import com.linagora.android.linshare.domain.usecases.receivedshare.ContextMenuReceivedShareClick
import com.linagora.android.linshare.domain.usecases.receivedshare.GetReceivedSharesOrderedInteractor
import com.linagora.android.linshare.operator.download.DownloadOperator
import com.linagora.android.linshare.operator.download.toDownloadRequest
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.action.OrderByActionImp
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ListItemBehavior
import com.linagora.android.linshare.view.myspace.MySpaceViewModel.Companion.NO_DOWNLOADING_DOCUMENT
import com.linagora.android.linshare.view.receivedshares.action.ReceivedSharesCopyInMySpaceContextMenu
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceivedSharesViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val getReceivedSharesOrderedInteractor: GetReceivedSharesOrderedInteractor,
    private val getOrderListConfigurationInteractor: GetOrderListConfigurationInteractor,
    private val copyInMySpaceInteractor: CopyInMySpaceInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val downloadOperator: DownloadOperator
) : BaseViewModel(internetAvailable, dispatcherProvider),
    ListItemBehavior<Share> {

    val downloadContextMenu = ReceivedShareDownloadContextMenu(this)

    val copyInMySpaceContextMenu = ReceivedSharesCopyInMySpaceContextMenu(this)

    val orderByAction = OrderByActionImp(this)

    override fun onContextMenuClick(data: Share) {
        dispatchState(Either.right(ContextMenuReceivedShareClick(data)))
    }

    fun getOrderListConfiguration() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getOrderListConfigurationInteractor(OrderListType.ReceivedShare))
        }
    }

    fun setCurrentOrderListConfigurationType(orderListConfigurationType: OrderListConfigurationType) {
        orderByAction.setCurrentOrderListConfigurationType(orderListConfigurationType)
    }

    fun getReceivedList() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getReceivedSharesOrderedInteractor(orderByAction.getCurrentOrderListConfigurationType()))
        }
    }

    fun getDownloading(): Share? {
        return downloadContextMenu.downloadingData.get()
    }

    fun downloadShare(credential: com.linagora.android.linshare.domain.model.Credential, token: Token, share: Share) {
        viewModelScope.launch(dispatcherProvider.io) {
            downloadContextMenu.setDownloading(NO_DOWNLOADING_DOCUMENT)
            downloadOperator.download(credential, token, share.toDownloadRequest())
        }
    }

    fun onSwipeRefresh() {
        getReceivedList()
    }

    fun copyInMySpace(share: Share) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(copyInMySpaceInteractor(share.toCopyRequest(SpaceType.RECEIVED_SHARE)))
        }
    }
}
