/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

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
