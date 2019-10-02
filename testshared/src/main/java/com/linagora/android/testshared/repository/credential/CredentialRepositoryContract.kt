package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.repository.CredentialRepository
import java.net.URL
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

abstract class CredentialRepositoryContract {

    private val USER_NAME = Username("alica@domain.com")
    private val USER_NAME2 = Username("bob@domain.com")
    private val SERVER_NAME = URL("http://domain.com")
    private val CREDENTIAL = Credential(SERVER_NAME, USER_NAME)
    private val CREDENTIAL2 = Credential(SERVER_NAME, USER_NAME2)

    abstract val credentialRepository: CredentialRepository

    @Test
    fun persistsCredentialShouldSaveCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            val credential = credentialRepository.getCredential()

            assertThat(credential).isEqualTo(CREDENTIAL)
        }
    }

    @Test
    fun persistsCredentialShouldUpdateCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            val credential = credentialRepository.getCredential()

            assertThat(credential).isEqualTo(CREDENTIAL2)
        }
    }

    @Test
    fun getCredentialShouldReturnEmptyWithNoneSavedCredential() {
        runBlockingTest {
            assertThat(credentialRepository.getCredential()).isNull()
        }
    }

    @Test
    fun getCredentialShouldReturnEmptyAfterClearingCredentials() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            credentialRepository.clearCredential()

            assertThat(credentialRepository.getCredential()).isNull()
        }
    }
}
