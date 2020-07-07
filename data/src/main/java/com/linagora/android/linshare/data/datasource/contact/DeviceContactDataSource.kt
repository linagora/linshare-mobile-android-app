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
