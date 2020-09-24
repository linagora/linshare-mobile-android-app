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
import com.linagora.android.linshare.functionality.FunctionalityObserver
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
    override val internetAvailable: ConnectionLiveData,
    private val getAuthenticatedInfo: GetAuthenticatedInfoInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authorizationManager: AuthorizationManager,
    private val writeStoragePermission: WriteStoragePermission,
    private val readContactPermission: ReadContactPermission,
    val functionalityObserver: FunctionalityObserver
) : BaseViewModel(internetAvailable, dispatcherProvider) {

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
