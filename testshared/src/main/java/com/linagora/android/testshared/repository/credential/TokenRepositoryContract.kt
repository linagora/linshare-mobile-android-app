package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.repository.TokenRepository
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

abstract class TokenRepositoryContract {

    val TOKEN_VALUE = "ZHBoYW1ob2FuZ0BsaW5hZ29yYS5jb206aThqckJ3KTgzNk4="
    val TOKEN_VALUE_2 = "ZHBoYW1ob2GzX0BsaW5hZ29yYS5jb206aThqckJ3KTgzNk4="
    private val TOKEN = Token(TOKEN_VALUE)
    private val TOKEN_2 = Token(TOKEN_VALUE_2)

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
