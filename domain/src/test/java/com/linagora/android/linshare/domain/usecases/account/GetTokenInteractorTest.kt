package com.linagora.android.linshare.domain.usecases.account

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.EMPTY_TOKEN_STATE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetTokenInteractorTest {

    @Mock
    private lateinit var tokenRepository: TokenRepository

    private lateinit var getToken: GetTokenInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getToken = GetTokenInteractor(tokenRepository)
    }

    @Test
    fun getTokenShouldSuccessWithRightCredential() {
        runBlockingTest {
            `when`(tokenRepository.getToken(LINSHARE_CREDENTIAL))
                .thenAnswer { TOKEN }

            val states = getToken(LINSHARE_CREDENTIAL)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE)).isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE)).isEqualTo(AUTHENTICATE_SUCCESS_STATE)
        }
    }

    @Test
    fun getTokenShouldNotEmitEndStateWithWrongCredential() {
        runBlockingTest {
            `when`(tokenRepository.getToken(LINSHARE_CREDENTIAL))
                .thenAnswer { null }

            val states = getToken(LINSHARE_CREDENTIAL)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE)).isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE)).isEqualTo(EMPTY_TOKEN_STATE)
        }
    }
}
