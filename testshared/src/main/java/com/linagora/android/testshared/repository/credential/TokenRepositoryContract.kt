package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.repository.TokenRepository
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

abstract class TokenRepositoryContract {

    private val TOKEN = Token("ZHBoYW1ob2FuZ0BsaW5hZ29yYS5jb206aThqckJ3KTgzNk4=")
    private val TOKEN_2 = Token("ZHBoYW1ob2GzX0BsaW5hZ29yYS5jb206aThqckJ3KTgzNk4=")

    abstract val tokenRepository: TokenRepository

    @Test
    fun persistsTokenShouldSaveToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TOKEN)

            assertThat(tokenRepository.getToken()).isEqualTo(TOKEN)
        }
    }

    @Test
    fun persistsTokenShouldUpdateToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TOKEN)
            tokenRepository.persistsToken(TOKEN_2)

            assertThat(tokenRepository.getToken()).isEqualTo(TOKEN_2)
        }
    }

    @Test
    fun getTokenShouldReturnEmptyWithNoneSavedToken() {
        runBlockingTest {
            assertThat(tokenRepository.getToken()).isNull()
        }
    }

    @Test
    fun getTokenShouldReturnEmptyAfterClearingToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TOKEN)
            tokenRepository.clearToken()

            assertThat(tokenRepository.getToken()).isNull()
        }
    }
}
