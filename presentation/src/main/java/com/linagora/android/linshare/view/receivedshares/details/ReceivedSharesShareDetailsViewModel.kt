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

package com.linagora.android.linshare.view.receivedshares.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareId
import com.linagora.android.linshare.domain.usecases.receivedshare.GetReceivedShareInteractor
import com.linagora.android.linshare.domain.usecases.receivedshare.GetReceivedShareSuccess
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ReceivedSharesShareDetailsViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
    private val getReceivedShareInteractor: GetReceivedShareInteractor
) : BaseViewModel(internetAvailable, coroutinesDispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReceivedSharesShareDetailsViewModel::class.java)
    }

    private val mutableShare = MutableLiveData<Share>()
    val share: LiveData<Share> = mutableShare

    fun retrieveShare(shareId: ShareId) {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            consumeStates(getReceivedShareInteractor(shareId))
        }
    }

    override fun onSuccessDispatched(success: Success) {
        LOGGER.info("onSuccessDispatched(): $success")
        when (success) {
            is GetReceivedShareSuccess -> mutableShare.value = success.share
        }
    }
}
