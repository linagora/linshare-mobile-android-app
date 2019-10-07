package com.linagora.android.linshare.domain.usecases.auth

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_2
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME2
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class AuthenticateInteractorTest {

    @Mock
    private lateinit var authenticationRepository: AuthenticationRepository

    private lateinit var authenticateInteractor: AuthenticateInteractor

    private val INIT_STATE = Either.Right(Idle)

    private val LOADING_STATE = Either.Right(Loading)

    private val AUTHENTICATE_SUCCESS_STATE = Either.Right(AuthenticationViewState(TOKEN))

    private val WRONG_CREDENTIAL_STATE = Either.Left(AuthenticationFailure(BadCredentials(WRONG_CREDENTIAL)))

    private val WRONG_PASSWORD_STATE = Either.Left(AuthenticationFailure(BadCredentials(WRONG_PASSWORD)))

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        authenticateInteractor = AuthenticateInteractor(authenticationRepository)
    }

    @Test
    fun authenticateShouldSuccessWithRightUsernamePassword() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, USER_NAME, PASSWORD))
                .thenAnswer { TOKEN }

            assertThat(authenticateInteractor(SERVER_URL, USER_NAME, PASSWORD)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, AUTHENTICATE_SUCCESS_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithWrongUrl() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, USER_NAME, PASSWORD))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))

            assertThat(authenticateInteractor(SERVER_URL, USER_NAME, PASSWORD)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithWrongUsername() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, USER_NAME2, PASSWORD))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))

            assertThat(authenticateInteractor(SERVER_URL, USER_NAME2, PASSWORD)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithWrongPassword() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, USER_NAME, PASSWORD_2))
                .thenThrow(BadCredentials(WRONG_PASSWORD))

            assertThat(authenticateInteractor(SERVER_URL, USER_NAME, PASSWORD_2)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, WRONG_PASSWORD_STATE)
        }
    }
}
