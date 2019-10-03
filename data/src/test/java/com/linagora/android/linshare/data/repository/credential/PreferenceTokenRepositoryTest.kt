package com.linagora.android.linshare.data.repository.credential

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.linagora.android.linshare.data.repository.credential.PreferenceTokenRepository.Key
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.testshared.repository.credential.TokenRepositoryContract
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PreferenceTokenRepositoryTest : TokenRepositoryContract() {

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: Editor

    private lateinit var tokenRepo: TokenRepository

    override val tokenRepository: TokenRepository
        get() = tokenRepo

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        tokenRepo = PreferenceTokenRepository(sharedPreferences)

        `when`(sharedPreferences.edit()).thenAnswer { editor }
        `when`(editor.putString(any(), any())).thenAnswer { editor }
    }

    @Test
    override fun persistsTokenShouldSaveToken() {
        `when`(sharedPreferences.getString(Key.TOKEN, null))
            .thenAnswer { TOKEN_VALUE }

        super.persistsTokenShouldSaveToken()
    }

    @Test
    override fun persistsTokenShouldUpdateToken() {
        `when`(sharedPreferences.getString(Key.TOKEN, null))
            .thenAnswer { TOKEN_VALUE_2 }

        super.persistsTokenShouldUpdateToken()
    }

    @Test
    override fun getTokenShouldReturnEmptyWithNoneSavedToken() {
        `when`(sharedPreferences.getString(Key.TOKEN, null))
            .thenAnswer { null }

        super.getTokenShouldReturnEmptyWithNoneSavedToken()
    }

    @Test
    override fun getTokenShouldReturnEmptyAfterClearingToken() {
        `when`(sharedPreferences.getString(Key.TOKEN, null))
            .thenAnswer { null }

        super.getTokenShouldReturnEmptyAfterClearingToken()
    }
}
