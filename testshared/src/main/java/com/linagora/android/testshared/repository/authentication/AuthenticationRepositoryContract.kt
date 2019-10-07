package com.linagora.android.testshared.repository.authentication

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.testshared.TestFixtures.Authentications.LINSHARE_PASSWORD1
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_2
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_USER1
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME2
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

abstract class AuthenticationRepositoryContract {

    abstract val authenticationRepository: AuthenticationRepository

    @Test
    open fun retrievePermanentTokenShouldSuccessWithRightUsernamePassword() {
        runBlockingTest {
            val token = authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1)

            assertThat(token).isEqualTo(TOKEN)
        }
    }

    @Test
    open fun retrievePermanentTokenShouldFailureWithWrongUrl() {
        val exception = assertThrows<BadCredentials> {
            runBlockingTest {
                authenticationRepository.retrievePermanentToken(SERVER_URL, USER_NAME2, LINSHARE_PASSWORD1)
            }
        }
        assertThat(exception.message).isEqualTo(WRONG_CREDENTIAL)
    }

    @Test
    open fun retrievePermanentTokenShouldFailureWithWrongUsername() {
        val exception = assertThrows<BadCredentials> {
            runBlockingTest {
                authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, USER_NAME2, LINSHARE_PASSWORD1)
            }
        }
        assertThat(exception.message).isEqualTo(WRONG_CREDENTIAL)
    }

    @Test
    open fun retrievePermanentTokenShouldFailureWithWrongPassword() {
        val exception = assertThrows<BadCredentials> {
            runBlockingTest {
                authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, PASSWORD_2)
            }
        }
        assertThat(exception.message).isEqualTo(WRONG_PASSWORD)
    }
}
