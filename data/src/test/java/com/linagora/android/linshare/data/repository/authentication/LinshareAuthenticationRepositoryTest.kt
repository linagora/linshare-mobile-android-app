package com.linagora.android.linshare.data.repository.authentication

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.LinshareDataSource
import com.linagora.android.linshare.data.repository.credential.MemoryCredentialRepository
import com.linagora.android.linshare.data.repository.credential.MemoryTokenRepository
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.testshared.TestFixtures.Authentications.LINSHARE_PASSWORD1
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_2
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_USER1
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME2
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN_VALUE
import com.linagora.android.testshared.repository.authentication.AuthenticationRepositoryContract
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
            Mockito.`when`(linshareDataSource.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1))
                .thenAnswer { Token(TOKEN_VALUE) }

            super.retrievePermanentTokenShouldSuccessWithRightUsernamePassword()
            assertThat(credentialRepository.getCredential()).isEqualTo(LINSHARE_CREDENTIAL)
            assertThat(tokenRepository.getToken(LINSHARE_CREDENTIAL))
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongUrl() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(SERVER_URL, USER_NAME2, LINSHARE_PASSWORD1))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))
            super.retrievePermanentTokenShouldFailureWithWrongUrl()
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongUsername() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(LINSHARE_BASE_URL, USER_NAME2, LINSHARE_PASSWORD1))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))
            super.retrievePermanentTokenShouldFailureWithWrongUsername()
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongPassword() {
        runBlockingTest {
            Mockito.`when`(linshareDataSource.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, PASSWORD_2))
                .thenThrow(BadCredentials(WRONG_PASSWORD))

            super.retrievePermanentTokenShouldFailureWithWrongPassword()
        }
    }
}
