package com.linagora.android.linshare.domain.repository.document

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentRequest

interface DocumentRepository {

    suspend fun upload(documentRequest: DocumentRequest): Document
}
