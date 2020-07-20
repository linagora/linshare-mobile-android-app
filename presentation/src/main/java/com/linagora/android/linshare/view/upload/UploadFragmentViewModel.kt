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

package com.linagora.android.linshare.view.upload

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.domain.usecases.share.AddMailingList
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.SelectDesinationClick
import com.linagora.android.linshare.domain.usecases.share.SelectUploadOutsideToMySpace
import com.linagora.android.linshare.domain.usecases.share.SelectUploadOutsideToSharedSpace
import com.linagora.android.linshare.model.upload.UploadDocumentRequest
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.DeviceStorageStats
import com.linagora.android.linshare.util.DeviceStorageStats.Companion.INTERNAL_ROOT
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.widget.ShareRecipientsManager
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadFragmentViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor,
    private val deviceStorageStats: DeviceStorageStats,
    val shareRecipientsManager: ShareRecipientsManager
) : BaseViewModel(internetAvailable, dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadFragmentViewModel::class.java)
    }

    fun checkAccountQuota(documentRequest: UploadDocumentRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(enoughAccountQuotaInteractor(documentRequest.uploadFileSize))
        }
    }

    fun checkLocalDeviceStorage(documentRequest: UploadDocumentRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            val deviceFreeSpace = deviceStorageStats.getDeviceStorageFreeSpace(INTERNAL_ROOT)
            documentRequest.takeIf { it.uploadFileSize >= deviceFreeSpace }
                ?.let { dispatchUIState(Either.left(NotEnoughDeviceStorageViewState)) }
                ?: dispatchUIState(Either.right(EnoughDeviceStorage(documentRequest)))
        }
    }

    fun addRecipient(user: GenericUser) {
        LOGGER.info("addRecipient() $user")
        if (shareRecipientsManager.addRecipient(user)) {
            dispatchState(Either.right(AddRecipient(user)))
        }
    }

    fun removeRecipient(user: GenericUser) {
        LOGGER.info("removeRecipient() $user")
        shareRecipientsManager.removeRecipient(user)
    }

    fun addMailingList(mailingList: MailingList) {
        LOGGER.info("addMailingList(): $mailingList")
        if (shareRecipientsManager.addMailingList(mailingList)) {
            dispatchState(Either.right(AddMailingList(mailingList)))
        }
    }

    fun removeMailingList(mailingList: MailingList) {
        LOGGER.info("removeMailingList(): $mailingList")
        shareRecipientsManager.removeMailingList(mailingList)
    }

    fun onUploadButtonClick(uploadDocumentRequest: UploadDocumentRequest) {
        dispatchState(Either.right(OnUploadButtonClick(uploadDocumentRequest)))
    }

    fun navigateDestination() {
        dispatchState(Either.right(SelectDesinationClick))
    }

    fun selectUploadOutsideToMySpace() {
        dispatchState(Either.right(SelectUploadOutsideToMySpace))
    }

    fun selectUploadOutsideToSharedSpace() {
        dispatchState(Either.right(SelectUploadOutsideToSharedSpace))
    }

    fun resetRecipientManager() = shareRecipientsManager.resetShareRecipientManager()
}
