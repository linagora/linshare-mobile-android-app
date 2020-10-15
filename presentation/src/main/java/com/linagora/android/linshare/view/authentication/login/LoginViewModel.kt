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

package com.linagora.android.linshare.view.authentication.login

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.network.SupportVersion
import com.linagora.android.linshare.domain.usecases.auth.AuthenticateInteractor
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.net.URL
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val baseUrlInterceptor: DynamicBaseUrlInterceptor,
    private val authenticateInteractor: AuthenticateInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(internetAvailable, dispatcherProvider) {

    companion object {
        private const val HTTPS_PREFIX = "https://"
        private val EMPTY = null
    }

    fun authenticate(baseUrl: URL, supportVersion: SupportVersion, username: Username, password: Password) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(authenticateInteractor(
                baseUrl = baseUrl,
                supportVersion = supportVersion,
                username = username,
                password = password)
            )
        }
    }

    fun authenticate(baseUrl: String, supportVersion: SupportVersion, username: String, password: String) {
        parseForm(baseUrl, username, password)
            ?.let { authenticate(it.first, supportVersion, it.second, it.third) }
    }

    override fun onSuccessDispatched(success: Success) {
        success.takeIf { it is AuthenticationViewState }
            ?.let { it as AuthenticationViewState }
            ?.let { setUpServiceBaseUrl(it.credential.serverUrl, it.credential.supportVersion) }
    }

    private fun setUpServiceBaseUrl(baseUrl: URL, supportVersion: SupportVersion) {
        baseUrlInterceptor.changeBaseUrl(baseUrl, supportVersion)
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
            require(username.isNotBlank())
            require(Patterns.EMAIL_ADDRESS.matcher(username).matches())
            Username(username)
        } catch (exp: Exception) {
            dispatchState(Either.Right(LoginFormState(
                errorMessage = R.string.email_is_required,
                errorType = ErrorType.WRONG_EMAIL
            )))
            EMPTY
        }
    }
}
