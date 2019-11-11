package com.linagora.android.linshare.domain.usecases.auth

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.SUCCESS_REMOVE_ACCOUNT_STATE
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.MockitoAnnotations

class RemoveAccountInteractorTest {

    @Mock
    lateinit var authenticationRepository: AuthenticationRepository

    @Mock
    lateinit var credentialRepository: CredentialRepository

    @Mock
    lateinit var tokenRepository: TokenRepository

    private lateinit var removeAccountInteractor: RemoveAccountInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        removeAccountInteractor = RemoveAccountInteractor(
            authenticationRepository,
            credentialRepository,
            tokenRepository
        )
    }

    @Test
    fun removeAccountShouldSuccessWithRightCredential() {
        runBlockingTest {
            `when`(tokenRepository.getToken(LINSHARE_CREDENTIAL))
                .thenAnswer { TOKEN }

            assertThat(removeAccountInteractor.invoke(LINSHARE_CREDENTIAL)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, SUCCESS_REMOVE_ACCOUNT_STATE)

            verify(authenticationRepository).deletePermanentToken(TOKEN)
            verify(credentialRepository).removeCredential(LINSHARE_CREDENTIAL)
        }
    }

    @Test
    fun removeAccountShouldSuccessWithTokenIsNotExist() {
        runBlockingTest {
            `when`(tokenRepository.getToken(LINSHARE_CREDENTIAL))
                .thenAnswer { null }

            assertThat(removeAccountInteractor.invoke(LINSHARE_CREDENTIAL)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, SUCCESS_REMOVE_ACCOUNT_STATE)

            verifyNoInteractions(authenticationRepository)
            verify(credentialRepository).removeCredential(LINSHARE_CREDENTIAL)
        }
    }
}
