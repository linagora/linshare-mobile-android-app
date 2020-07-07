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

import androidx.lifecycle.Observer
import arrow.core.Either
import com.linagora.android.linshare.CoroutinesExtension
import com.linagora.android.linshare.InstantExecutorExtension
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.usecases.auth.AuthenticateInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.runBlockingTest
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.utils.provideFakeCoroutinesDispatcherProvider
import com.linagora.android.testshared.TestFixtures.Authentications.LINSHARE_PASSWORD1
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_VALUE
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_USER1
import com.linagora.android.testshared.TestFixtures.Credentials.NAME
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.WRONG_CREDENTIAL_STATE
import kotlinx.coroutines.flow.flow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExtendWith(
    InstantExecutorExtension::class
)
class LoginViewModelTest {

    @Mock
    lateinit var baseUrlInterceptor: DynamicBaseUrlInterceptor

    @Mock
    lateinit var authenticateInteractor: AuthenticateInteractor

    @Mock
    lateinit var viewObserver: Observer<Either<Failure, Success>>

    @Mock
    lateinit var internetAvailable: ConnectionLiveData

    private lateinit var loginViewModel: LoginViewModel

    companion object {
        @JvmField
        @RegisterExtension
        val coroutinesExtension = CoroutinesExtension()
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        loginViewModel = LoginViewModel(
            internetAvailable = internetAvailable,
            baseUrlInterceptor = baseUrlInterceptor,
            authenticateInteractor = authenticateInteractor,
            dispatcherProvider = provideFakeCoroutinesDispatcherProvider(coroutinesExtension.testDispatcher)
        )
    }

    @Test
    fun authenticateShouldSuccessWithRightCredential() {
        coroutinesExtension.runBlockingTest {
            Mockito.`when`(authenticateInteractor(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            loginViewModel.viewState.observeForever(viewObserver)

            loginViewModel.authenticate(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1)

            `verify`(viewObserver).onChanged(LOADING_STATE)
            `verify`(viewObserver).onChanged(AUTHENTICATE_SUCCESS_STATE)
            `verify`(baseUrlInterceptor).changeBaseUrl(LINSHARE_BASE_URL)
        }
    }

    @Test
    fun authenticateShouldFailedWithWrongBaseURL() {
        coroutinesExtension.runBlockingTest {
            Mockito.`when`(authenticateInteractor(SERVER_URL, LINSHARE_USER1, LINSHARE_PASSWORD1))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { WRONG_CREDENTIAL_STATE }
                    }
                }

            loginViewModel.viewState.observeForever(viewObserver)

            loginViewModel.authenticate(SERVER_URL, LINSHARE_USER1, LINSHARE_PASSWORD1)

            `verify`(viewObserver).onChanged(LOADING_STATE)
            `verify`(viewObserver).onChanged(WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun authenticateShouldFailedWithWrongUsername() {
        coroutinesExtension.runBlockingTest {
            Mockito.`when`(authenticateInteractor(LINSHARE_BASE_URL, LINSHARE_USER1, PASSWORD))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { WRONG_CREDENTIAL_STATE }
                    }
                }

            loginViewModel.viewState.observeForever(viewObserver)

            loginViewModel.authenticate(LINSHARE_BASE_URL, LINSHARE_USER1, PASSWORD)

            `verify`(viewObserver).onChanged(LOADING_STATE)
            `verify`(viewObserver).onChanged(WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun authenticateShouldFailedWithWrongPassword() {
        coroutinesExtension.runBlockingTest {
            Mockito.`when`(authenticateInteractor(LINSHARE_BASE_URL, USER_NAME, LINSHARE_PASSWORD1))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { WRONG_CREDENTIAL_STATE }
                    }
                }

            loginViewModel.viewState.observeForever(viewObserver)

            loginViewModel.authenticate(LINSHARE_BASE_URL, USER_NAME, LINSHARE_PASSWORD1)

            `verify`(viewObserver).onChanged(LOADING_STATE)
            `verify`(viewObserver).onChanged(WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun authenticateShouldNoticeWhenUrlEmpty() {
        loginViewModel.viewState.observeForever(viewObserver)

        loginViewModel.authenticate("", NAME, PASSWORD_VALUE)

        `verify`(viewObserver).onChanged(Either.Right(LoginFormState(
            errorMessage = R.string.wrong_url,
            errorType = ErrorType.WRONG_URL
        )))
    }

    @Test
    fun loginFormChangedShouldNoticeWhenUsernameEmpty() {
        loginViewModel.viewState.observeForever(viewObserver)

        loginViewModel.authenticate("linsahre.domain", "", PASSWORD_VALUE)

        `verify`(viewObserver).onChanged(Either.Right(LoginFormState(
            errorMessage = R.string.email_is_required,
            errorType = ErrorType.WRONG_EMAIL
        )))
    }

    @Test
    fun loginFormChangedShouldNoticeWhenUsernameIsNotEmail() {
        loginViewModel.viewState.observeForever(viewObserver)

        loginViewModel.authenticate("linsahre.domain", "linshare", PASSWORD_VALUE)

        `verify`(viewObserver).onChanged(Either.Right(LoginFormState(
            errorMessage = R.string.email_is_required,
            errorType = ErrorType.WRONG_EMAIL
        )))
    }

    @Test
    fun loginFormChangedShouldNoticeWhenPasswordWrong() {
        loginViewModel.viewState.observeForever(viewObserver)

        loginViewModel.authenticate("linsahre.domain", NAME, "")

        `verify`(viewObserver).onChanged(Either.Right(LoginFormState(
            errorMessage = R.string.credential_error_message,
            errorType = ErrorType.WRONG_CREDENTIAL
        )))
    }
}
