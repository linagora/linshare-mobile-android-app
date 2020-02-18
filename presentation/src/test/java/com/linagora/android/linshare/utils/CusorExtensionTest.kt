
package com.linagora.android.linshare.utils

import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.util.MimeType
import com.linagora.android.linshare.util.getDocumentRequest
import com.linagora.android.testshared.TestFixtures.DocumentRequests.DOCUMENT_REQUEST
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboCursor

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])

class CusorExtensionTest {

    @Test
    fun getDocumentRequestShouldReturnADocumentWhenAllColumnAreNormal() {

        val uri = DOCUMENT_REQUEST.uri

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.fileName, DOCUMENT_REQUEST.fileSize, DOCUMENT_REQUEST.mediaType)))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Images.Media.MIME_TYPE))

        assertThat(cursor.getDocumentRequest(uri)).isEqualTo(DOCUMENT_REQUEST)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeNull() {

        val documentRequestExpect = DocumentRequest(DOCUMENT_REQUEST.uri, DOCUMENT_REQUEST.fileName, DOCUMENT_REQUEST.fileSize, MimeType.APPLICATION_DEFAULT.toMediaType())

        val uri = DOCUMENT_REQUEST.uri

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.fileName, DOCUMENT_REQUEST.fileSize, null)))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Images.Media.MIME_TYPE))

        assertThat(cursor.getDocumentRequest(uri)).isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeBlank() {

        val documentRequestExpect = DocumentRequest(DOCUMENT_REQUEST.uri, DOCUMENT_REQUEST.fileName, DOCUMENT_REQUEST.fileSize, MimeType.APPLICATION_DEFAULT.toMediaType())

        val uri = DOCUMENT_REQUEST.uri

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.fileName, DOCUMENT_REQUEST.fileSize, "")))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Images.Media.MIME_TYPE))

        assertThat(cursor.getDocumentRequest(uri)).isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeWrong() {

        val documentRequestExpect = DocumentRequest(DOCUMENT_REQUEST.uri, DOCUMENT_REQUEST.fileName, DOCUMENT_REQUEST.fileSize, MimeType.APPLICATION_DEFAULT.toMediaType())

        val uri = DOCUMENT_REQUEST.uri

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.fileName, DOCUMENT_REQUEST.fileSize, "asdasfdsdg")))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Images.Media.MIME_TYPE))

        assertThat(cursor.getDocumentRequest(uri)).isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveNullName() {

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(null, DOCUMENT_REQUEST.fileSize, DOCUMENT_REQUEST.mediaType)))
        cursor.setColumnNames(
            listOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Images.Media.MIME_TYPE))

        assertThrows<IllegalStateException> { (cursor.getDocumentRequest(DOCUMENT_REQUEST.uri)) }
    }

    @Test
    fun getDocumentRequestWhenCursorHaveNullSize() {

        val documentRequestExpect = DocumentRequest(DOCUMENT_REQUEST.uri, DOCUMENT_REQUEST.fileName, 0, DOCUMENT_REQUEST.mediaType)

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.fileName, null, DOCUMENT_REQUEST.mediaType)))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Images.Media.MIME_TYPE))

        assertThat(cursor.getDocumentRequest(DOCUMENT_REQUEST.uri)).isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveWrongSize() {

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.fileName, -9999, DOCUMENT_REQUEST.mediaType)))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, MediaStore.Images.Media.MIME_TYPE))

        assertThrows<IllegalArgumentException> { (cursor.getDocumentRequest(DOCUMENT_REQUEST.uri)) }
    }
}
