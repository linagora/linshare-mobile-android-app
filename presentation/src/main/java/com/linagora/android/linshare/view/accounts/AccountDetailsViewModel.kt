package com.linagora.android.linshare.view.accounts

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.usecases.account.GetTokenInteractor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountDetailsViewModel @Inject constructor(
    private val getToken: GetTokenInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    fun retrieveAccountDetails(credential: Credential) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getToken(credential = credential))
        }
    }
}
