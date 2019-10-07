package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.repository.CredentialRepository
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import java.net.URL

abstract class CredentialRepositoryContract {

    val NAME = "alica@domain.com"
    val NAME2 = "bob@domain.com"
    val SERVER_NAME = "http://domain.com"

    private val USER_NAME = Username(NAME)
    private val USER_NAME2 = Username(NAME2)
    private val SERVER_URL = URL(SERVER_NAME)
    private val CREDENTIAL = Credential(SERVER_URL, USER_NAME)
    private val CREDENTIAL2 = Credential(SERVER_URL, USER_NAME2)

    abstract val credentialRepository: CredentialRepository

    @Test
    open fun persistsCredentialShouldSaveCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            val credential = credentialRepository.getCredential()

            assertThat(credential).isEqualTo(CREDENTIAL)
        }
    }

    @Test
    open fun persistsCredentialShouldUpdateCredential() {
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
