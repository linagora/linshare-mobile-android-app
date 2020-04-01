package com.linagora.android.linshare.data.datasource

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.usecases.upload.UploadException

interface DocumentDataSource {

    @Throws(UploadException::class)
    suspend fun upload(
        documentRequest: DocumentRequest,
        onTransfer: OnTransfer
    ): Document

    suspend fun getAll(): List<Document>

    suspend fun remove(documentId: DocumentId): Document

    suspend fun search(query: QueryString): List<Document>

    suspend fun share(shareRequest: ShareRequest): List<Share>
}
