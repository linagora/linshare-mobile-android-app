package com.linagora.android.linshare.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.AUTHENTICATED
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.UNAUTHENTICATED
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val getAuthenticatedInfo: GetAuthenticatedInfoInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authorizationManager: AuthorizationManager
) : BaseViewModel(dispatcherProvider) {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        INVALID_AUTHENTICATION
    }

    val authenticationState = MutableLiveData<AuthenticationState>()

    init {
        authenticationState.value = UNAUTHENTICATED
    }

    fun checkSignedIn() {
        dispatchState(INITIAL_STATE)
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAuthenticatedInfo())
        }
    }

    override fun onSuccessDispatched(success: Success) {
        success.takeIf { it is AuthenticationViewState }
            ?.let {
                authenticationState.value = AUTHENTICATED
                setUpInterceptors(it as AuthenticationViewState)
            }
    }

    override fun onFailureDispatched(failure: Failure) {
        authenticationState.value = INVALID_AUTHENTICATION
    }

    private fun setUpInterceptors(authenticationViewState: AuthenticationViewState) {
        dynamicBaseUrlInterceptor.changeBaseUrl(authenticationViewState.credential.serverUrl)
        authorizationManager.updateToken(authenticationViewState.token)
    }
}
