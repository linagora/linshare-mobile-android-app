package com.linagora.android.linshare.domain.usecases.auth

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_2
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME2
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.WRONG_CREDENTIAL_STATE
import com.linagora.android.testshared.TestFixtures.State.WRONG_PASSWORD_STATE
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
