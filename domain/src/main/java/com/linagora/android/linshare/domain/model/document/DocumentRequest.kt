package com.linagora.android.linshare.domain.model.document

import android.net.Uri
import okhttp3.MediaType

data class DocumentRequest(
    val uri: Uri,
    val fileName: String,
    val fileSize: Long,
    val mediaType: MediaType
) {
    init {
        require(fileSize >= 0) { "fileSize must not be negative" }
    }
}
