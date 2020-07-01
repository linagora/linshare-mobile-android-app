package com.linagora.android.linshare.view

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.Initial
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowReadContact
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.permission.ReadContactPermission
import com.linagora.android.linshare.permission.WriteStoragePermission
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.AUTHENTICATED
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.UNAUTHENTICATED
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    val internetAvailable: ConnectionLiveData,
    private val getAuthenticatedInfo: GetAuthenticatedInfoInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authorizationManager: AuthorizationManager,
    private val writeStoragePermission: WriteStoragePermission,
    private val readContactPermission: ReadContactPermission
) : BaseViewModel(dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainActivityViewModel::class.java)

        private val EMPTY_AUTHENTICATION = null
    }

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        INVALID_AUTHENTICATION
    }

    private val mutableCurrentAuthentication = MutableLiveData<AuthenticationViewState>()
    val currentAuthentication: LiveData<AuthenticationViewState> = mutableCurrentAuthentication

    private val shouldShowPermissionRequest = MutableLiveData<RuntimePermissionRequest>(Initial)
    val shouldShowPermissionRequestState: LiveData<RuntimePermissionRequest> = shouldShowPermissionRequest

    val authenticationState = MutableLiveData<AuthenticationState>()

    init {
        authenticationState.value = UNAUTHENTICATED
    }

    fun checkSignedIn() {
        LOGGER.info("checkSignedIn()")
        dispatchState(INITIAL_STATE)
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAuthenticatedInfo())
        }
    }

    fun shouldShowWriteStoragePermissionRequest(activity: Activity) {
        shouldShowPermissionRequest.value = Initial
        viewModelScope.launch(dispatcherProvider.io) {
            val shouldShow = writeStoragePermission.shouldShowPermissionRequest(
                writeStoragePermission.systemShouldShowPermissionRequest(activity))
            shouldShowPermissionRequest.postValue(shouldShow)
        }
    }

    fun setActionForWriteStoragePermissionRequest(previousUserPermissionAction: PreviousUserPermissionAction) {
        viewModelScope.launch(dispatcherProvider.io) {
            writeStoragePermission.setActionForPermissionRequest(previousUserPermissionAction)
        }
    }

    fun checkWriteStoragePermission(context: Context): PermissionResult {
        return writeStoragePermission.checkSelfPermission(context)
    }

    fun setUpAuthenticated(authenticationViewState: AuthenticationViewState) {
        authenticationState.value = AUTHENTICATED
        mutableCurrentAuthentication.value = authenticationViewState
        setUpInterceptors(authenticationViewState)
    }

    override fun onSuccessDispatched(success: Success) {
        success.takeIf { it is AuthenticationViewState }
            ?.let { setUpAuthenticated(it as AuthenticationViewState) }
    }

    override fun onFailureDispatched(failure: Failure) {
        authenticationState.value = INVALID_AUTHENTICATION
        mutableCurrentAuthentication.value = EMPTY_AUTHENTICATION
    }

    private fun setUpInterceptors(authenticationViewState: AuthenticationViewState) {
        LOGGER.info("setUpInterceptors()")
        dynamicBaseUrlInterceptor.changeBaseUrl(authenticationViewState.credential.serverUrl)
        authorizationManager.updateToken(authenticationViewState.token)
    }

    fun shouldShowReadContactPermissionRequest(activity: Activity) {
        if (shouldShowPermissionRequest.value != ShouldNotShowReadContact) {
            shouldShowPermissionRequest.value = Initial
            viewModelScope.launch(dispatcherProvider.io) {
                val shouldShow = readContactPermission.shouldShowPermissionRequest(
                    readContactPermission.systemShouldShowPermissionRequest(activity)
                )
                shouldShowPermissionRequest.postValue(shouldShow)
            }
        }
    }

    fun setActionForReadContactPermissionRequest(previousUserPermissionAction: PreviousUserPermissionAction) {
        previousUserPermissionAction.takeIf { it == PreviousUserPermissionAction.DENIED }
            ?.let { shouldShowPermissionRequest.value = ShouldNotShowReadContact }
        viewModelScope.launch(dispatcherProvider.io) {
            readContactPermission.setActionForPermissionRequest(previousUserPermissionAction)
        }
    }

    fun checkReadContactPermission(context: Context): PermissionResult {
        return readContactPermission.checkSelfPermission(context)
    }
}
