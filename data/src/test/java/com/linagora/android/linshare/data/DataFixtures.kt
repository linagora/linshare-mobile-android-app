package com.linagora.android.linshare.data

import com.linagora.android.linshare.domain.model.document.DocumentRequest
import okhttp3.MediaType
import java.io.File

object DataFixtures {

    private const val TEST_FILE_NAME = "test.txt"

    private val FILE = File(ClassLoader.getSystemResource(TEST_FILE_NAME).file)

    val DOCUMENT_REQUEST = DocumentRequest(
        file = FILE,
        uploadFileName = "document.txt",
        mediaType = MediaType.get("text/plain")
    )
}
