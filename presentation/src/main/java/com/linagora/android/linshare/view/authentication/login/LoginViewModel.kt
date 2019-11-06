package com.linagora.android.linshare.view.authentication.login

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.usecases.auth.AuthenticateInteractor
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.net.URL
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val baseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authenticateInteractor: AuthenticateInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    companion object {
        private const val HTTPS_PREFIX = "https://"
        private val EMPTY = null
    }

    fun authenticate(baseUrl: URL, username: Username, password: Password) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(authenticateInteractor(
                baseUrl = baseUrl,
                username = username,
                password = password)
            )
        }
    }

    fun authenticate(baseUrl: String, username: String, password: String) {
        parseForm(baseUrl, username, password)
            ?.let { authenticate(it.first, it.second, it.third) }
    }

    override fun onSuccessDispatched(success: Success) {
        success.takeIf { it is AuthenticationViewState }
            ?.let { it as AuthenticationViewState }
            ?.let { setUpServiceBaseUrl(it.credential.serverUrl) }
    }

    private fun setUpServiceBaseUrl(baseUrl: URL) {
        baseUrlInterceptor.changeBaseUrl(baseUrl)
    }

    private fun parseForm(url: String, username: String, password: String): Triple<URL, Username, Password>? {
        val parsedUrl = parseOrNoticeUrlError(url)
        val parseUsername = parseOrNoticeUsernameError(username)
        val parsePassword = parseOrNoticePasswordError(password)

        return runCatching {
            Triple(parsedUrl!!, parseUsername!!, parsePassword!!)
        }.getOrDefault(EMPTY)
    }

    private fun validateUrl(url: String): URL {
        require(url.isNotBlank())
        val fullUrl = url.takeIf { it.toHttpUrlOrNull() == null }
            ?.let { HTTPS_PREFIX + url }
            ?: url

        return URL(fullUrl)
    }

    private fun parseOrNoticeUrlError(url: String): URL? {
        return try {
            validateUrl(url)
        } catch (exp: Exception) {
            dispatchState(Either.Right(LoginFormState(
                errorMessage = R.string.wrong_url,
                errorType = ErrorType.WRONG_URL
            )))
            EMPTY
        }
    }

    private fun parseOrNoticePasswordError(password: String): Password? {
        return try {
            Password(password)
        } catch (exp: Exception) {
            dispatchState(Either.Right(LoginFormState(
                errorMessage = R.string.credential_error_message,
                errorType = ErrorType.WRONG_CREDENTIAL
            )))
            EMPTY
        }
    }

    private fun parseOrNoticeUsernameError(username: String): Username? {
        return try {
            Username(username)
        } catch (exp: Exception) {
            dispatchState(Either.Right(LoginFormState(
                errorMessage = R.string.credential_error_message,
                errorType = ErrorType.WRONG_CREDENTIAL
            )))
            EMPTY
        }
    }
}
