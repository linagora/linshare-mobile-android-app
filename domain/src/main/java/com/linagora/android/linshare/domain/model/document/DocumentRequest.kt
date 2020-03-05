package com.linagora.android.linshare.domain.model.document

import okhttp3.MediaType
import java.io.File

data class DocumentRequest(
    val file: File,
    val uploadFileName: String,
    val mediaType: MediaType
) {
    init {
        require(uploadFileName.isNotBlank()) { "file name must not be blank" }
    }
}
