package com.linagora.android.linshare.data.repository.credential

import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.testshared.repository.credential.TokenRepositoryContract
import org.junit.jupiter.api.BeforeEach

class MemoryTokenRepositoryTest : TokenRepositoryContract() {

    private lateinit var tokenRepo: TokenRepository

    override val tokenRepository: TokenRepository
        get() = tokenRepo

    @BeforeEach
    fun setUp() {
        tokenRepo = MemoryTokenRepository()
    }
}
