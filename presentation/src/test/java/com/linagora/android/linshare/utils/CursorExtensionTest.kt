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

package com.linagora.android.linshare.utils

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.model.upload.UploadDocumentRequest
import com.linagora.android.linshare.util.MimeType
import com.linagora.android.linshare.util.getUploadDocumentRequest
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboCursor

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CursorExtensionTest {

    companion object {

        private const val TEST_FILE_NAME = "test.txt"

        private val COLUMN_NAMES = listOf(
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE,
            MediaStore.Images.Media.MIME_TYPE)

        private val DOCUMENT_URI = Uri.parse("content://0@media/external/file/276")

        private val UPLOAD_DOCUMENT_REQUEST = UploadDocumentRequest(
            uploadUri = DOCUMENT_URI,
            uploadFileSize = 11,
            uploadFileName = "document.txt",
            uploadMediaType = "text/plain".toMediaType()
        )
    }

    @Test
    fun getDocumentRequestShouldReturnADocumentWhenAllColumnAreNormal() {
        val cursor = RoboCursor()

        cursor.setColumnNames(COLUMN_NAMES)
        cursor.setResults(arrayOf(arrayOf(
            UPLOAD_DOCUMENT_REQUEST.uploadFileName,
            UPLOAD_DOCUMENT_REQUEST.uploadFileSize,
            UPLOAD_DOCUMENT_REQUEST.uploadMediaType)))

        cursor.moveToFirst()

        assertThat(cursor.getUploadDocumentRequest(DOCUMENT_URI))
            .isEqualTo(UPLOAD_DOCUMENT_REQUEST)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeNull() {
        val documentRequestExpect = UploadDocumentRequest(
            DOCUMENT_URI,
            UPLOAD_DOCUMENT_REQUEST.uploadFileSize,
            UPLOAD_DOCUMENT_REQUEST.uploadFileName,
            MimeType.APPLICATION_DEFAULT.toMediaType())

        val cursor = RoboCursor()
        cursor.setColumnNames(COLUMN_NAMES)
        cursor.setResults(arrayOf(arrayOf(
            UPLOAD_DOCUMENT_REQUEST.uploadFileName,
            UPLOAD_DOCUMENT_REQUEST.uploadFileSize,
            null)))

        cursor.moveToFirst()

        assertThat(cursor.getUploadDocumentRequest(DOCUMENT_URI))
            .isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeBlank() {
        val documentRequestExpect = UploadDocumentRequest(
            DOCUMENT_URI,
            UPLOAD_DOCUMENT_REQUEST.uploadFileSize,
            UPLOAD_DOCUMENT_REQUEST.uploadFileName,
            MimeType.APPLICATION_DEFAULT.toMediaType())

        val cursor = RoboCursor()
        cursor.setColumnNames(COLUMN_NAMES)
        cursor.setResults(arrayOf(arrayOf(
            UPLOAD_DOCUMENT_REQUEST.uploadFileName,
            UPLOAD_DOCUMENT_REQUEST.uploadFileSize,
            "")))

        cursor.moveToFirst()

        assertThat(cursor.getUploadDocumentRequest(DOCUMENT_URI))
            .isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeWrong() {
        val documentRequestExpect = UploadDocumentRequest(
            DOCUMENT_URI,
            UPLOAD_DOCUMENT_REQUEST.uploadFileSize,
            UPLOAD_DOCUMENT_REQUEST.uploadFileName,
            MimeType.APPLICATION_DEFAULT.toMediaType())

        val cursor = RoboCursor()
        cursor.setColumnNames(COLUMN_NAMES)
        cursor.setResults(arrayOf(arrayOf(
            UPLOAD_DOCUMENT_REQUEST.uploadFileName,
            UPLOAD_DOCUMENT_REQUEST.uploadFileSize,
            "wrong")))

        cursor.moveToFirst()

        assertThat(cursor.getUploadDocumentRequest(DOCUMENT_URI))
            .isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestShouldThrowWhenCursorHaveNullName() {
        val cursor = RoboCursor()

        cursor.setColumnNames(COLUMN_NAMES)
        cursor.setResults(arrayOf(arrayOf(
            null,
            UPLOAD_DOCUMENT_REQUEST.uploadFileSize,
            "wrong")))

        cursor.moveToFirst()

        assertThrows<IllegalStateException> { (cursor.getUploadDocumentRequest(DOCUMENT_URI)) }
    }
}
