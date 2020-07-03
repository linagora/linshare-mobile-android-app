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
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.widget.ShareRecipientsManager
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadFragmentViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor,
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
