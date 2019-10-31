package com.linagora.android.linshare.data.repository.credential

import android.content.SharedPreferences
import com.linagora.android.linshare.data.repository.credential.PreferenceCredentialRepository.Key
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.testshared.TestFixtures.Credentials.NAME
import com.linagora.android.testshared.TestFixtures.Credentials.NAME2
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_NAME
import com.linagora.android.testshared.repository.credential.CredentialRepositoryContract
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PreferenceCredentialRepositoryTest : CredentialRepositoryContract() {

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var credentialRepo: CredentialRepository

    override val credentialRepository: CredentialRepository
        get() = credentialRepo

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        credentialRepo = PreferenceCredentialRepository(sharedPreferences)

        `when`(sharedPreferences.edit()).thenAnswer { editor }
    }

    @Test
    override fun persistsCredentialShouldNotSaveDuplicateCredential() {
        `when`(sharedPreferences.getString(Key.SERVER_NAME, null))
            .thenAnswer { SERVER_NAME }
        `when`(sharedPreferences.getString(Key.USER_NAME, null))
            .thenAnswer { NAME }

        super.persistsCredentialShouldNotSaveDuplicateCredential()
    }

    @Test
    override fun persistsCredentialShouldSaveCredential() {
        `when`(sharedPreferences.getString(Key.SERVER_NAME, null))
            .thenAnswer { SERVER_NAME }
        `when`(sharedPreferences.getString(Key.USER_NAME, null))
            .thenAnswer { NAME }

        super.persistsCredentialShouldSaveCredential()
    }

    @Test
    override fun persistsCredentialShouldUpdateCredential() {
        `when`(sharedPreferences.getString(Key.SERVER_NAME, null))
            .thenAnswer { SERVER_NAME }
        `when`(sharedPreferences.getString(Key.USER_NAME, null))
            .thenAnswer { NAME2 }

        super.persistsCredentialShouldUpdateCredential()
    }

    @Test
    override fun persistCredentialShouldSetCurrentCredentialForTheLastSavedCredential() {
        `when`(sharedPreferences.getString(Key.SERVER_NAME, null))
            .thenAnswer { SERVER_NAME }
        `when`(sharedPreferences.getString(Key.USER_NAME, null))
            .thenAnswer { NAME2 }

        super.persistCredentialShouldSetCurrentCredentialForTheLastSavedCredential()
    }

    @Test
    override fun removeCredentialShouldRemoveExactlyCredential() {
        `when`(sharedPreferences.getString(Key.SERVER_NAME, null))
            .thenAnswer { SERVER_NAME }
        `when`(sharedPreferences.getString(Key.USER_NAME, null))
            .thenAnswer { NAME2 }

        super.removeCredentialShouldRemoveExactlyCredential()
    }

    @Test
    override fun setCurrentCredentialShouldSuccess() {
        `when`(sharedPreferences.getString(Key.SERVER_NAME, null))
            .thenAnswer { SERVER_NAME }
        `when`(sharedPreferences.getString(Key.USER_NAME, null))
            .thenAnswer { NAME2 }

        super.setCurrentCredentialShouldSuccess()
    }

    @Test
    override fun setCurrentCredentialShouldFailedWithNotSavedCredential() {
        `when`(sharedPreferences.getString(Key.SERVER_NAME, null))
            .thenAnswer { SERVER_NAME }
        `when`(sharedPreferences.getString(Key.USER_NAME, null))
            .thenAnswer { NAME }

        super.setCurrentCredentialShouldFailedWithNotSavedCredential()
    }

    @Test
    override fun setCurrentCredentialShouldNotUpdateWithNotSavedCredential() {
        `when`(sharedPreferences.getString(Key.SERVER_NAME, null))
            .thenAnswer { SERVER_NAME }
        `when`(sharedPreferences.getString(Key.USER_NAME, null))
            .thenAnswer { NAME }

        super.setCurrentCredentialShouldNotUpdateWithNotSavedCredential()
    }
}
