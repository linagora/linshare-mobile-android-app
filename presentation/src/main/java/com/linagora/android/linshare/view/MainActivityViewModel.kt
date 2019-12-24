package com.linagora.android.linshare.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.getOrElse
import com.linagora.android.linshare.domain.model.properties.UserStoragePermissionRequest
import com.linagora.android.linshare.domain.model.properties.UserStoragePermissionRequest.DENIED
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.properties.StoragePermissionRequest
import com.linagora.android.linshare.model.properties.StoragePermissionRequest.SHOULD_NOT_SHOW
import com.linagora.android.linshare.model.properties.StoragePermissionRequest.SHOULD_SHOW
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.AUTHENTICATED
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.UNAUTHENTICATED
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val getAuthenticatedInfo: GetAuthenticatedInfoInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authorizationManager: AuthorizationManager,
    private val propertiesRepository: PropertiesRepository
) : BaseViewModel(dispatcherProvider) {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        INVALID_AUTHENTICATION
    }

    val authenticationState = MutableLiveData<AuthenticationState>()

    private val shouldShowPermissionRequest = MutableLiveData(StoragePermissionRequest.INITIAL)
    val shouldShowPermissionRequestState: LiveData<StoragePermissionRequest> = shouldShowPermissionRequest

    init {
        authenticationState.value = UNAUTHENTICATED
    }

    fun checkSignedIn() {
        dispatchState(INITIAL_STATE)
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAuthenticatedInfo())
        }
    }

    fun shouldShowPermissionRequest(systemStoragePermissionRequest: StoragePermissionRequest) {
        shouldShowPermissionRequest.value = StoragePermissionRequest.INITIAL
        viewModelScope.launch(dispatcherProvider.io) {
            flowOf(systemStoragePermissionRequest)
                .zip(
                    other = flowOf(propertiesRepository.getDeniedStoragePermission()),
                    transform = { systemShowPermission, deniedStorage ->
                        transformDeniedStoragePermissionState(deniedStorage, systemShowPermission) }
                ).collect { shouldShowPermissionRequest.postValue(it) }
        }
    }

    fun setUserStoragePermissionRequest(userStoragePermissionRequest: UserStoragePermissionRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            propertiesRepository.storeDeniedStoragePermission(userStoragePermissionRequest)
        }
    }

    private fun transformDeniedStoragePermissionState(
        userStoragePermissionRequest: UserStoragePermissionRequest,
        systemStoragePermissionRequest: StoragePermissionRequest
    ): StoragePermissionRequest {
        return Either.cond(
            test = userStoragePermissionRequest != DENIED || systemStoragePermissionRequest == SHOULD_SHOW,
            ifTrue = { SHOULD_SHOW },
            ifFalse = { SHOULD_NOT_SHOW }
        ).getOrElse { SHOULD_NOT_SHOW }
    }

    fun setUpAuthenticated(authenticationViewState: AuthenticationViewState) {
        authenticationState.value = AUTHENTICATED
        setUpInterceptors(authenticationViewState)
    }

    override fun onSuccessDispatched(success: Success) {
        success.takeIf { it is AuthenticationViewState }
            ?.let { setUpAuthenticated(it as AuthenticationViewState) }
    }

    override fun onFailureDispatched(failure: Failure) {
        authenticationState.value = INVALID_AUTHENTICATION
    }

    private fun setUpInterceptors(authenticationViewState: AuthenticationViewState) {
        dynamicBaseUrlInterceptor.changeBaseUrl(authenticationViewState.credential.serverUrl)
        authorizationManager.updateToken(authenticationViewState.token)
    }
}
