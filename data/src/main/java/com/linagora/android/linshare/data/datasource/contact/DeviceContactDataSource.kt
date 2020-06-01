package com.linagora.android.linshare.data.datasource.contact

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
import android.provider.ContactsContract.Data
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.contact.Contact
import com.linagora.android.linshare.domain.model.contact.SimpleContact
import org.slf4j.LoggerFactory
import javax.inject.Inject

class DeviceContactDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) : ContactDataSource {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DeviceContactDataSource::class.java)

        private val CONTACT_SUGGESTION_PROJECTION = arrayOf(
            Data.DISPLAY_NAME_PRIMARY,
            Email.ADDRESS
        )

        private val EMPTY_SELECTION = null

        private const val CONTACT_SUGGESTION_SORT_BY = Data.DISPLAY_NAME_PRIMARY
    }

    override suspend fun getContactsSuggestion(autoCompletePattern: AutoCompletePattern): List<Contact> {
        val filterUri = Uri.withAppendedPath(
            Email.CONTENT_FILTER_URI,
            Uri.encode(autoCompletePattern.value)
        )
        return contentResolver
            .query(filterUri, CONTACT_SUGGESTION_PROJECTION, EMPTY_SELECTION, EMPTY_SELECTION, CONTACT_SUGGESTION_SORT_BY)
            .use { cursor -> cursor?.let(this@DeviceContactDataSource::iterateCursor) ?: emptyList() }
    }

    private fun iterateCursor(cursor: Cursor): List<Contact> {
        return generateSequence { cursor.takeIf { it.moveToNext() } }
            .map { extractContact(it) }
            .filterNotNull()
            .toList()
    }

    private fun extractContact(cursor: Cursor): Contact? {
        return runCatching {
                val displayName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME_PRIMARY))
                val email = cursor.getString(cursor.getColumnIndex(Email.ADDRESS))
                SimpleContact(displayName, email) }
            .onFailure { LOGGER.error("extractContact(): ${it.message} - ${it.printStackTrace()}") }
            .getOrNull()
    }
}
