package com.linagora.android.linshare.view.upload

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.widget.ShareRecipientsManager
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadFragmentViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor,
    val shareRecipientsManager: ShareRecipientsManager
) : BaseViewModel(dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadFragmentViewModel::class.java)
    }

    fun checkAccountQuota(documentRequest: DocumentRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(enoughAccountQuotaInteractor(documentRequest))
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

    fun resetRecipients() = shareRecipientsManager.resetRecipients()
}
