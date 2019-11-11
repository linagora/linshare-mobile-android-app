package com.linagora.android.linshare.data.repository.authentication

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.LinshareDataSource
import com.linagora.android.linshare.data.repository.credential.MemoryCredentialRepository
import com.linagora.android.linshare.data.repository.credential.MemoryTokenRepository
import com.linagora.android.linshare.domain.network.Endpoint
import com.linagora.android.linshare.domain.network.withServicePath
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.CONNECT_ERROR
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.EMPTY_TOKEN
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.SERVER_NOT_FOUND
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.UNKNOWN
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.ConnectError
import com.linagora.android.linshare.domain.usecases.auth.EmptyToken
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFound
import com.linagora.android.linshare.domain.usecases.auth.UnknownError
import com.linagora.android.testshared.TestFixtures.Authentications.LINSHARE_PASSWORD1
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_2
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_USER1
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME2
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import com.linagora.android.testshared.repository.authentication.AuthenticationRepositoryContract
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class LinshareAuthenticationRepositoryTest : AuthenticationRepositoryContract() {

    private lateinit var linshareAuthenticationRepository: LinshareAuthenticationRepository
    private lateinit var credentialRepository: MemoryCredentialRepository
    private lateinit var tokenRepository: MemoryTokenRepository

    @Mock
    private lateinit var linshareDataSource: LinshareDataSource

    override val authenticationRepository: AuthenticationRepository
        get() = linshareAuthenticationRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        credentialRepository = MemoryCredentialRepository()
        tokenRepository = MemoryTokenRepository()

        linshareAuthenticationRepository = LinshareAuthenticationRepository(
            linshareDataSource,
            credentialRepository,
            tokenRepository
        )
    }

    @Test
    override fun retrievePermanentTokenShouldSuccessWithRightUsernamePassword() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION),
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenAnswer { TOKEN }

            super.retrievePermanentTokenShouldSuccessWithRightUsernamePassword()
            assertThat(credentialRepository.getAllCredential()).containsExactly(LINSHARE_CREDENTIAL)
            assertThat(tokenRepository.getToken(LINSHARE_CREDENTIAL))
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongUrl() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = SERVER_URL.withServicePath(Endpoint.AUTHENTICAION),
                    username = USER_NAME2,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))
            super.retrievePermanentTokenShouldFailureWithWrongUrl()
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongUsername() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION),
                    username = USER_NAME2,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))
            super.retrievePermanentTokenShouldFailureWithWrongUsername()
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongPassword() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION),
                    username = LINSHARE_USER1,
                    password = PASSWORD_2))
                .thenThrow(BadCredentials(WRONG_PASSWORD))

            super.retrievePermanentTokenShouldFailureWithWrongPassword()
        }
    }

    @Test
    fun retrievePermanentTokenShouldFailureWithEmptyToken() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION),
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(EmptyToken)

            val exception = assertThrows<EmptyToken> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.message).isEqualTo(EMPTY_TOKEN)
        }
    }

    @Test
    fun retrievePermanentTokenShouldFailureWithServerNotFound() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION),
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(ServerNotFound)

            val exception = assertThrows<ServerNotFound> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.message).isEqualTo(SERVER_NOT_FOUND)
        }
    }

    @Test
    fun retrievePermanentTokenShouldFailureWithUnknownError() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION),
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(UnknownError)

            val exception = assertThrows<UnknownError> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.message).isEqualTo(UNKNOWN)
        }
    }

    @Test
    fun retrievePermanentTokenShouldFailureWithConnectError() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL.withServicePath(Endpoint.AUTHENTICAION),
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(ConnectError)

            val exception = assertThrows<ConnectError> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.message).isEqualTo(CONNECT_ERROR)
        }
    }
}
