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
