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
