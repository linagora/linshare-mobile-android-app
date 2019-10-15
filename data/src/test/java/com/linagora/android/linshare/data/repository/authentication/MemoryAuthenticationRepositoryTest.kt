package com.linagora.android.linshare.data.repository.authentication

import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.testshared.TestFixtures.Authentications.LINSHARE_PASSWORD1
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import com.linagora.android.testshared.repository.authentication.AuthenticationRepositoryContract
import org.junit.jupiter.api.BeforeEach

class MemoryAuthenticationRepositoryTest : AuthenticationRepositoryContract() {

    private lateinit var authenticationRepo: AuthenticationRepository

    override val authenticationRepository: AuthenticationRepository
        get() = authenticationRepo

    @BeforeEach
    fun setUp() {
        authenticationRepo = MemoryAuthenticationRepository(LINSHARE_CREDENTIAL, LINSHARE_PASSWORD1, TOKEN)
    }
}
