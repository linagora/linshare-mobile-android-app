
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
