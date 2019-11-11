package com.linagora.android.linshare.data.repository.credential

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.testshared.TestFixtures
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferenceCredentialRepositoryTest {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var credentialRepository: PreferenceCredentialRepository

    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        credentialRepository = PreferenceCredentialRepository(sharedPreferences)
    }

    @Test
    fun persistsCredentialShouldNotSaveDuplicateCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).hasSize(1)
            assertThat(credentials).containsExactly(TestFixtures.Credentials.CREDENTIAL)
        }
    }

    @Test
    fun persistsCredentialShouldSaveCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).containsExactly(TestFixtures.Credentials.CREDENTIAL)
        }
    }

    @Test
    fun persistsCredentialShouldUpdateCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL2)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).contains(TestFixtures.Credentials.CREDENTIAL2)
        }
    }

    @Test
    fun persistCredentialShouldSetCurrentCredentialForTheLastSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL2)

            val currentCredential = credentialRepository.getCurrentCredential()

            assertThat(currentCredential).isEqualTo(TestFixtures.Credentials.CREDENTIAL2)
        }
    }

    @Test
    fun removeCredentialShouldRemoveExactlyCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL2)

            credentialRepository.removeCredential(TestFixtures.Credentials.CREDENTIAL)

            val credential = credentialRepository.getAllCredential()

            assertThat(credential).containsExactly(TestFixtures.Credentials.CREDENTIAL2)
        }
    }

    @Test
    fun setCurrentCredentialShouldSuccess() {
        runBlockingTest {
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL2)

            credentialRepository.setCurrentCredential(TestFixtures.Credentials.CREDENTIAL2)

            val credential = credentialRepository.getCurrentCredential()

            assertThat(credential).isEqualTo(TestFixtures.Credentials.CREDENTIAL2)
        }
    }

    @Test
    fun setCurrentCredentialShouldFailedWithNotSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)

            assertThat(credentialRepository.setCurrentCredential(TestFixtures.Credentials.CREDENTIAL2))
                .isFalse()
        }
    }

    @Test
    fun setCurrentCredentialShouldNotUpdateWithNotSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)

            credentialRepository.setCurrentCredential(TestFixtures.Credentials.CREDENTIAL2)

            val credential = credentialRepository.getCurrentCredential()

            assertThat(credential).isEqualTo(TestFixtures.Credentials.CREDENTIAL)
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
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL2)

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
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL)
            credentialRepository.persistsCredential(TestFixtures.Credentials.CREDENTIAL2)

            credentialRepository.clearCredential()

            assertThat(credentialRepository.getAllCredential())
                .isEqualTo(emptySet<Credential>())
        }
    }

    @After
    fun tearDown() {
        runBlockingTest {
            credentialRepository.clearCredential()
        }
    }
}
