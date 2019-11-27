package com.linagora.android.linshare.data.datasource

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.upload.UploadException

interface DocumentDataSource {

    @Throws(UploadException::class)
    suspend fun upload(documentRequest: DocumentRequest): Document
}
