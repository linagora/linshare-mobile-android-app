package com.linagora.android.linshare.domain.repository.document

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.upload.OnTransfer

interface DocumentRepository {

    suspend fun upload(
        documentRequest: DocumentRequest,
        onTransfer: OnTransfer
    ): Document

    suspend fun getAll(): List<Document>

    suspend fun remove(documentId: DocumentId): Document

    suspend fun search(query: QueryString): List<Document>
}
