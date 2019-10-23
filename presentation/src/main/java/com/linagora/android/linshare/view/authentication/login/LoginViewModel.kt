package com.linagora.android.linshare.view.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.usecases.auth.AuthenticateInteractor
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationFailure
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.network.Endpoint
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.withServicePath
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val baseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authenticateInteractor: AuthenticateInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    private val mutableLoginFormValidation = MutableLiveData(false)
    val loginFormValidation: LiveData<Boolean> = mutableLoginFormValidation

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

    fun authenticate(baseUrl: String, username: String, password: String) {
        try {
            parseForm(baseUrl, username, password)
                .apply { authenticate(this.first, this.second, this.third) }
        } catch (malformedExp: MalformedURLException) {
            dispatchState(Either.left(AuthenticationFailure(
                BadCredentials(malformedExp.message ?: WRONG_CREDENTIAL)))
            )
        } catch (illegalExp: IllegalArgumentException) {
            dispatchState(Either.left(AuthenticationFailure(
                BadCredentials(illegalExp.message ?: WRONG_CREDENTIAL)))
            )
        }
    }

    private fun setUpServiceBaseUrl(baseUrl: URL) {
        baseUrlInterceptor.changeBaseUrl(baseUrl)
    }

    fun loginFormChanged(url: String, username: String, password: String) {
        mutableLoginFormValidation.value = runCatching {
            parseForm(url, username, password)
            true
        }.getOrDefault(false)
    }

    private fun parseForm(url: String, username: String, password: String): Triple<URL, Username, Password> {
        return Triple(URL(url), Username(username), Password(password))
    }
}
