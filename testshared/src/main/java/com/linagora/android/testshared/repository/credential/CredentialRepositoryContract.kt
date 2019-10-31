package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

abstract class CredentialRepositoryContract {

    abstract val credentialRepository: CredentialRepository

    @Test
    open fun persistsCredentialShouldNotSaveDuplicateCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).hasSize(1)
            assertThat(credentials).containsExactly(CREDENTIAL)
        }
    }

    @Test
    open fun persistsCredentialShouldSaveCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).containsExactly(CREDENTIAL)
        }
    }

    @Test
    open fun persistsCredentialShouldUpdateCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).contains(CREDENTIAL2)
        }
    }

    @Test
    open fun persistCredentialShouldSetCurrentCredentialForTheLastSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            val currentCredential = credentialRepository.getCurrentCredential()

            assertThat(currentCredential).isEqualTo(CREDENTIAL2)
        }
    }

    @Test
    open fun removeCredentialShouldRemoveExactlyCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            credentialRepository.removeCredential(CREDENTIAL)

            val credential = credentialRepository.getAllCredential()

            assertThat(credential).containsExactly(CREDENTIAL2)
        }
    }

    @Test
    open fun setCurrentCredentialShouldSuccess() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL2)

            credentialRepository.setCurrentCredential(CREDENTIAL2)

            val credential = credentialRepository.getCurrentCredential()

            assertThat(credential).isEqualTo(CREDENTIAL2)
        }
    }

    @Test
    open fun setCurrentCredentialShouldFailedWithNotSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            assertThat(credentialRepository.setCurrentCredential(CREDENTIAL2))
                .isFalse()
        }
    }

    @Test
    open fun setCurrentCredentialShouldNotUpdateWithNotSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            credentialRepository.setCurrentCredential(CREDENTIAL2)

            val credential = credentialRepository.getCurrentCredential()

            assertThat(credential).isEqualTo(CREDENTIAL)
        }
    }

    @Test
    fun getCurrentCredentialShouldReturnEmptyWithNoSavedCredential() {
        runBlockingTest {
            assertThat(credentialRepository.getCurrentCredential())
                .isNull()
        }
    }

    @Test
    fun getCurrentCredentialShouldReturnEmptyAfterClearCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            credentialRepository.clearCredential()

            assertThat(credentialRepository.getCurrentCredential())
                .isNull()
        }
    }

    @Test
    fun getAllCredentialShouldReturnEmptyWithNoneSavedCredential() {
        runBlockingTest {
            assertThat(credentialRepository.getAllCredential())
                .isEqualTo(emptySet<Credential>())
        }
    }

    @Test
    fun getAllCredentialShouldReturnEmptyAfterClearingCredentials() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            credentialRepository.clearCredential()

            assertThat(credentialRepository.getAllCredential())
                .isEqualTo(emptySet<Credential>())
        }
    }
}
