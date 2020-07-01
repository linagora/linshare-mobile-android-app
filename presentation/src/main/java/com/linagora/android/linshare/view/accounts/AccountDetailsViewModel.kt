package com.linagora.android.linshare.view.accounts

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.usecases.account.GetAccountDetailsInteractor
import com.linagora.android.linshare.domain.usecases.auth.RemoveAccountInteractor
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountDetailsViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val getAccountDetails: GetAccountDetailsInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val removeAccountInteractor: RemoveAccountInteractor,
    private val dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authorizationManager: AuthorizationManager
) : BaseViewModel(internetAvailable, dispatcherProvider) {

    fun retrieveAccountDetails(credential: Credential) {
        dynamicBaseUrlInterceptor.changeBaseUrl(credential.serverUrl)
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAccountDetails(credential = credential))
        }
    }

    fun removeAccount(credential: Credential) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(removeAccountInteractor(
                credential = credential
            ))
        }
    }

    fun resetInterceptors() {
        dynamicBaseUrlInterceptor.reset()
        authorizationManager.reset()
    }
}
