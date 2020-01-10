package com.linagora.android.linshare.data.datasource.network

import com.google.common.truth.Truth.assertThat
import okhttp3.MediaType
import okio.Buffer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileNotFoundException

class MeasurableUploadRequestBodyTest {

    companion object {
        const val TEST_FILE_NAME = "test.txt"
    }

    @Test
    fun writeToShouldWriteFileToBuffer() {
        val uploadingFile = File(ClassLoader.getSystemResource(TEST_FILE_NAME).file)
        var lastTransferred = 0L
        val requestBody = MeasurableUploadRequestBody(
            contentType = MediaType.get("text/plain"),
            file = uploadingFile,
            onTransfer = { transferredBytes, totalBytes ->
                lastTransferred = transferredBytes.value
                assertThat(transferredBytes.value).isAtLeast(0)
                assertThat(totalBytes.value).isEqualTo(uploadingFile.length())
            }
        )
        val buffer = Buffer()

        requestBody.writeTo(buffer)

        assertThat(lastTransferred).isEqualTo(uploadingFile.length())
    }

    @Test
    fun writeToShouldThrowExceptionWhenFileNotFound() {
        val uploadingFile = File("abc.txt")
        val requestBody = MeasurableUploadRequestBody(
            contentType = MediaType.get("text/plain"),
            file = uploadingFile,
            onTransfer = { transferredBytes, totalBytes ->
                assertThat(transferredBytes.value).isAtLeast(0)
                assertThat(totalBytes.value).isEqualTo(uploadingFile.length())
            }
        )
        val buffer = Buffer()

        assertThrows<FileNotFoundException> { requestBody.writeTo(buffer) }
    }

    @Test
    fun writeToShouldUpdateUploadProgress() {
        val uploadingFile = File(ClassLoader.getSystemResource(TEST_FILE_NAME).file)
        val requestBody = MeasurableUploadRequestBody(
            contentType = MediaType.get("text/plain"),
            file = uploadingFile,
            onTransfer = { transferredBytes, totalBytes ->
                assertThat(transferredBytes.value).isAtLeast(0)
                assertThat(totalBytes.value).isEqualTo(uploadingFile.length())
            }
        )
        val buffer = Buffer()

        requestBody.writeTo(buffer)
    }
}
