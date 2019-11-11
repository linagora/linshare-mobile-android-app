package com.linagora.android.linshare.data.repository.credential

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.testshared.TestFixtures
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL

@RunWith(AndroidJUnit4::class)
class PreferenceTokenRepositoryTest {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tokenRepository: PreferenceTokenRepository

    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenRepository = PreferenceTokenRepository(sharedPreferences)
    }

    @Test
    fun persistsTokenShouldSaveToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TestFixtures.Credentials.CREDENTIAL, TestFixtures.Tokens.TOKEN)

            assertThat(tokenRepository.getToken(TestFixtures.Credentials.CREDENTIAL))
                .isEqualTo(TestFixtures.Tokens.TOKEN)
        }
    }

    @Test
    fun persistsTokenShouldUpdateToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TestFixtures.Credentials.CREDENTIAL, TestFixtures.Tokens.TOKEN)
            tokenRepository.persistsToken(TestFixtures.Credentials.CREDENTIAL2, TestFixtures.Tokens.TOKEN_2)

            assertThat(tokenRepository.getToken(TestFixtures.Credentials.CREDENTIAL2))
                .isEqualTo(TestFixtures.Tokens.TOKEN_2)
        }
    }

    @Test
    fun getTokenShouldReturnEmptyWithNoneSavedToken() {
        runBlockingTest {
            assertThat(tokenRepository.getToken(TestFixtures.Credentials.CREDENTIAL)).isNull()
        }
    }

    @Test
    fun getTokenShouldReturnEmptyAfterClearingToken() {
        runBlockingTest {
            tokenRepository.persistsToken(TestFixtures.Credentials.CREDENTIAL, TestFixtures.Tokens.TOKEN)
            tokenRepository.removeToken(TestFixtures.Credentials.CREDENTIAL)

            assertThat(tokenRepository.getToken(TestFixtures.Credentials.CREDENTIAL)).isNull()
        }
    }

    @Test
    fun getTokenShouldNotReturnWithNotMatchedCredential() {
        runBlockingTest {
            tokenRepository.persistsToken(TestFixtures.Credentials.CREDENTIAL, TestFixtures.Tokens.TOKEN)

            assertThat(tokenRepository
                    .getToken(Credential(
                        URL("http://domain.com"),
                        Username("joe_token_key"))))
                .isNull()
        }
    }

    @Test
    fun getTokenShouldReturnATokenWithSpecialUsername() {
        runBlockingTest {
            val credential = Credential(LINSHARE_BASE_URL, Username("john_token_key"))

            tokenRepository.persistsToken(credential, TOKEN)

            val token = tokenRepository.getToken(credential)
            assertThat(token).isEqualTo(TOKEN)
        }
    }
}
