package com.linagora.android.linshare.domain.repository.document

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import java.util.UUID

interface DocumentRepository {

    suspend fun upload(
        documentRequest: DocumentRequest,
        onTransfer: OnTransfer
    ): Document

    suspend fun getAll(): List<Document>

    suspend fun remove(uuid: UUID): Document
}
