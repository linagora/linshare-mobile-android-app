package com.linagora.android.linshare.view.upload

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class UploadFragmentViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor
) : BaseViewModel(dispatcherProvider) {

    fun checkAccountQuota(documentRequest: DocumentRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(enoughAccountQuotaInteractor(documentRequest))
        }
    }
}
