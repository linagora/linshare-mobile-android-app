package com.linagora.android.linshare.data.repository.document

import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import javax.inject.Inject

class DocumentRepositoryImp @Inject constructor(
    private val linShareDocumentDataSource: DocumentDataSource
) : DocumentRepository {

    override suspend fun upload(
        documentRequest: DocumentRequest
    ): Document {
        return linShareDocumentDataSource.upload(documentRequest)
    }
}