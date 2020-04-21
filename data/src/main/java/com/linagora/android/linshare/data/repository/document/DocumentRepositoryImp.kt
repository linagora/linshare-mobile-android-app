package com.linagora.android.linshare.data.repository.document

import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import javax.inject.Inject

class DocumentRepositoryImp @Inject constructor(
    private val linShareDocumentDataSource: DocumentDataSource
) : DocumentRepository {

    override suspend fun upload(
        documentRequest: DocumentRequest,
        onTransfer: OnTransfer
    ): Document {
        return linShareDocumentDataSource.upload(documentRequest, onTransfer)
    }

    override suspend fun getAll(): List<Document> {
        return linShareDocumentDataSource.getAll()
    }

    override suspend fun remove(documentId: DocumentId): Document {
        return linShareDocumentDataSource.remove(documentId)
    }

    override suspend fun search(query: QueryString): List<Document> {
        return linShareDocumentDataSource.search(query)
    }

    override suspend fun share(shareRequest: ShareRequest): List<Share> {
        return linShareDocumentDataSource.share(shareRequest)
    }

    override suspend fun copy(copyRequest: CopyRequest): List<Document> {
        return linShareDocumentDataSource.copy(copyRequest)
    }
}
