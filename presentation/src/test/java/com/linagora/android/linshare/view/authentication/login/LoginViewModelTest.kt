package com.linagora.android.linshare.view.authentication.login

import androidx.lifecycle.Observer
import arrow.core.Either
import com.linagora.android.linshare.CoroutinesExtension
import com.linagora.android.linshare.InstantExecutorExtension
import com.linagora.android.linshare.domain.usecases.auth.AuthenticateInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.network.Endpoint
import com.linagora.android.linshare.runBlockingTest
import com.linagora.android.linshare.util.withServicePath
import com.linagora.android.linshare.utils.provideFakeCoroutinesDispatcherProvider
import com.linagora.android.testshared.TestFixtures.Authentications.LINSHARE_PASSWORD1
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_USER1
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

    private lateinit var loginViewModel: LoginViewModel

    companion object {
        @JvmField
        @RegisterExtension
        val coroutinesExtension = CoroutinesExtension()
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        loginViewModel =
            LoginViewModel(
                baseUrlInterceptor = baseUrlInterceptor,
                authenticateInteractor = authenticateInteractor,
                dispatcherProvider = provideFakeCoroutinesDispatcherProvider(coroutinesExtension.testDispatcher)
            )
    }

    @Test
    fun authenticateShouldSuccessWithRightCredential() {
        coroutinesExtension.runBlockingTest {
            Mockito.`when`(authenticateInteractor(LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION), LINSHARE_USER1, LINSHARE_PASSWORD1))
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
            Mockito.`when`(authenticateInteractor(SERVER_URL.withServicePath(Endpoint.AUTHENTICAION), LINSHARE_USER1, LINSHARE_PASSWORD1))
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
            Mockito.`when`(authenticateInteractor(LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION), LINSHARE_USER1, PASSWORD))
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
            Mockito.`when`(authenticateInteractor(LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION), USER_NAME, LINSHARE_PASSWORD1))
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
}
