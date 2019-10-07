package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN_2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

abstract class TokenRepositoryContract {

    abstract val tokenRepository: TokenRepository

    @Test
    open fun persistsTokenShouldSaveToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TOKEN)

            assertThat(tokenRepository.getToken()).isEqualTo(TOKEN)
        }
    }

    @Test
    open fun persistsTokenShouldUpdateToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TOKEN)
            tokenRepository.persistsToken(TOKEN_2)

            assertThat(tokenRepository.getToken()).isEqualTo(TOKEN_2)
        }
    }

    @Test
    open fun getTokenShouldReturnEmptyWithNoneSavedToken() {
        runBlockingTest {
            assertThat(tokenRepository.getToken()).isNull()
        }
    }

    @Test
    open fun getTokenShouldReturnEmptyAfterClearingToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TOKEN)
            tokenRepository.clearToken()

            assertThat(tokenRepository.getToken()).isNull()
        }
    }
}
