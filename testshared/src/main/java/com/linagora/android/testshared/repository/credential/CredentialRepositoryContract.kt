package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

abstract class CredentialRepositoryContract {

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
