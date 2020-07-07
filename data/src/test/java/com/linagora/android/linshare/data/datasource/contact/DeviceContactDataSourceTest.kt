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

package com.linagora.android.linshare.data.datasource.contact

import android.content.ContentResolver
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.Data
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.testshared.ShareFixtures.CONTACT_1
import com.linagora.android.testshared.ShareFixtures.CONTACT_2
import com.linagora.android.testshared.ShareFixtures.CONTACT_3
import com.linagora.android.testshared.ShareFixtures.CONTACT_4
import com.linagora.android.testshared.extension.MockitoUtils.any
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboCursor

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DeviceContactDataSourceTest {

    companion object {
        private val COLUMN_NAMES = listOf(
            Data.DISPLAY_NAME_PRIMARY,
            Email.ADDRESS
        )
    }

    @Mock
    lateinit var contentResolver: ContentResolver

    private lateinit var suggestionsCursor: RoboCursor

    private lateinit var deviceContactDataSource: DeviceContactDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        deviceContactDataSource = DeviceContactDataSource(contentResolver)
        suggestionsCursor = RoboCursor()
        suggestionsCursor.setColumnNames(COLUMN_NAMES)
    }

    @Test
    fun getContactsSuggestionShouldReturnMatchedContacts() {
        suggestionsCursor.setResults(arrayOf(
            arrayOf(CONTACT_1.displayName, CONTACT_1.email),
            arrayOf(CONTACT_2.displayName, CONTACT_2.email),
            arrayOf(CONTACT_3.displayName, CONTACT_3.email),
            arrayOf(CONTACT_4.displayName, CONTACT_4.email))
        )

        `when`(contentResolver.query(any(), any(), any(), any(), any()))
            .thenAnswer { suggestionsCursor }

        runBlockingTest {
            val suggestions = deviceContactDataSource.getContactsSuggestion(AutoCompletePattern("bar"))

            assertThat(suggestions).hasSize(4)
            assertThat(suggestions).containsExactly(CONTACT_1, CONTACT_2, CONTACT_3, CONTACT_4)
        }
    }

    @Test
    fun getContactsSuggestionShouldReturnEmptyListWhenNotFoundMatchedContact() {
        suggestionsCursor.setResults(emptyArray())

        `when`(contentResolver.query(any(), any(), any(), any(), any()))
            .thenAnswer { suggestionsCursor }

        runBlockingTest {
            val suggestions = deviceContactDataSource.getContactsSuggestion(AutoCompletePattern("bar"))

            assertThat(suggestions).isEmpty()
        }
    }

    @Test
    fun getContactsSuggestionShouldReturnMatchedContactsExceptContactHaveInvalidField() {
        suggestionsCursor.setResults(arrayOf(
            arrayOf(CONTACT_1.displayName, CONTACT_1.email),
            arrayOf(CONTACT_2.displayName, null),
            arrayOf(CONTACT_3.displayName, CONTACT_3.email),
            arrayOf(CONTACT_4.displayName, CONTACT_4.email))
        )

        `when`(contentResolver.query(any(), any(), any(), any(), any()))
            .thenAnswer { suggestionsCursor }

        runBlockingTest {
            val suggestions = deviceContactDataSource.getContactsSuggestion(AutoCompletePattern("bar"))

            assertThat(suggestions).hasSize(3)
            assertThat(suggestions).containsExactly(CONTACT_1, CONTACT_3, CONTACT_4)
        }
    }

    @Test
    fun getContactsSuggestionShouldReturnMatchedContactsExceptContactsHaveInvalidFields() {
        suggestionsCursor.setResults(arrayOf(
            arrayOf(CONTACT_1.displayName, CONTACT_1.email),
            arrayOf(CONTACT_2.displayName, null),
            arrayOf<String?>(null, null),
            arrayOf(CONTACT_4.displayName, CONTACT_4.email))
        )

        `when`(contentResolver.query(any(), any(), any(), any(), any()))
            .thenAnswer { suggestionsCursor }

        runBlockingTest {
            val suggestions = deviceContactDataSource.getContactsSuggestion(AutoCompletePattern("bar"))

            assertThat(suggestions).hasSize(2)
            assertThat(suggestions).containsExactly(CONTACT_1, CONTACT_4)
        }
    }
}
