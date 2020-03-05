
package com.linagora.android.linshare.utils

import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.util.MimeType
import com.linagora.android.linshare.util.getDocumentRequest
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboCursor
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CursorExtensionTest {

    companion object {

        private const val TEST_FILE_NAME = "test.txt"

        private val FILE = File(ClassLoader.getSystemResource(TEST_FILE_NAME).file)

        private val DOCUMENT_REQUEST = DocumentRequest(
            file = FILE,
            uploadFileName = "document.txt",
            mediaType = "text/plain".toMediaType()
        )
    }

    @Test
    fun getDocumentRequestShouldReturnADocumentWhenAllColumnAreNormal() {
        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.uploadFileName, DOCUMENT_REQUEST.mediaType)))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE))
        cursor.moveToFirst()

        assertThat(cursor.getDocumentRequest(FILE)).isEqualTo(DOCUMENT_REQUEST)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeNull() {
        val documentRequestExpect = DocumentRequest(FILE, DOCUMENT_REQUEST.uploadFileName, MimeType.APPLICATION_DEFAULT.toMediaType())

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.uploadFileName, null)))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE))
        cursor.moveToFirst()

        assertThat(cursor.getDocumentRequest(FILE)).isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeBlank() {
        val documentRequestExpect = DocumentRequest(FILE, DOCUMENT_REQUEST.uploadFileName, MimeType.APPLICATION_DEFAULT.toMediaType())

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.uploadFileName, "")))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE))
        cursor.moveToFirst()

        assertThat(cursor.getDocumentRequest(FILE)).isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestWhenCursorHaveMimeTypeWrong() {
        val documentRequestExpect = DocumentRequest(FILE, DOCUMENT_REQUEST.uploadFileName, MimeType.APPLICATION_DEFAULT.toMediaType())

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(DOCUMENT_REQUEST.uploadFileName, "asdasfdsdg")))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE))
        cursor.moveToFirst()

        assertThat(cursor.getDocumentRequest(FILE)).isEqualTo(documentRequestExpect)
    }

    @Test
    fun getDocumentRequestShouldThrowWhenCursorHaveNullName() {

        val cursor = RoboCursor()
        cursor.setResults(arrayOf(arrayOf(null, DOCUMENT_REQUEST.mediaType)))
        cursor.setColumnNames(listOf(OpenableColumns.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE))
        cursor.moveToFirst()

        assertThrows<IllegalStateException> { (cursor.getDocumentRequest(FILE)) }
    }
}
