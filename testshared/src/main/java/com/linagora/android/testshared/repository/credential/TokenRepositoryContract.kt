package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL2
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN_2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

abstract class TokenRepositoryContract {

    abstract val tokenRepository: TokenRepository

    @Test
    open fun persistsTokenShouldSaveToken() {
        runBlockingTest {
            tokenRepository.persistsToken(CREDENTIAL, TOKEN)

            assertThat(tokenRepository.getToken(CREDENTIAL)).isEqualTo(TOKEN)
        }
    }

    @Test
    open fun persistsTokenShouldUpdateToken() {
        runBlockingTest {
            tokenRepository.persistsToken(CREDENTIAL, TOKEN)
            tokenRepository.persistsToken(CREDENTIAL2, TOKEN_2)

            assertThat(tokenRepository.getToken(CREDENTIAL2)).isEqualTo(TOKEN_2)
        }
    }

    @Test
    open fun getTokenShouldReturnEmptyWithNoneSavedToken() {
        runBlockingTest {
            assertThat(tokenRepository.getToken(CREDENTIAL)).isNull()
        }
    }

    @Test
    open fun getTokenShouldReturnEmptyAfterClearingToken() {
        runBlockingTest {
            tokenRepository.persistsToken(CREDENTIAL, TOKEN)
            tokenRepository.removeToken(CREDENTIAL)

            assertThat(tokenRepository.getToken(CREDENTIAL)).isNull()
        }
    }

    @Test
    open fun getTokenShouldNotReturnWithNotMatchedCredential() {
        runBlockingTest {
            tokenRepository.persistsToken(CREDENTIAL, TOKEN)

            assertThat(tokenRepository.getToken(CREDENTIAL2)).isNull()
        }
    }
}
