package com.linagora.android.linshare.view.authentication

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.usecases.auth.AuthenticateInteractor
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.network.Endpoint
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.withServicePath
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

class LoginActivityViewModel @Inject constructor(
    private val baseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authenticateInteractor: AuthenticateInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    fun authenticate(baseUrl: URL, username: Username, password: Password) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(authenticateInteractor(
                baseUrl = baseUrl.withServicePath(Endpoint.AUTHENTICAION),
                username = username,
                password = password)
            )
            setUpServiceBaseUrl(baseUrl)
        }
    }

    private fun setUpServiceBaseUrl(baseUrl: URL) {
        baseUrlInterceptor.changeBaseUrl(baseUrl)
    }
}
