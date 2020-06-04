package com.linagora.android.linshare.model.upload

import android.content.Context
import android.net.Uri
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.util.createTempFile
import okhttp3.MediaType

data class UploadDocumentRequest(
    val uploadUri: Uri,
    val uploadFileSize: Long,
    val uploadFileName: String,
    val uploadMediaType: MediaType
) {
    init {
        require(uploadFileName.isNotBlank()) { "file name must not be blank" }
    }
}

fun UploadDocumentRequest.toDocumentRequest(context: Context): DocumentRequest {
    return DocumentRequest(uploadUri.createTempFile(context), uploadFileName, uploadMediaType)
}
