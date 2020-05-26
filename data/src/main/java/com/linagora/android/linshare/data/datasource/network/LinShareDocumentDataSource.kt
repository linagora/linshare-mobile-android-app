package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.data.network.NetworkExecutor
import com.linagora.android.linshare.data.network.handler.CopyNetworkRequestHandler
import com.linagora.android.linshare.data.network.handler.UploadNetworkRequestHandler
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.document.nameContains
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.model.sharedspace.PartParameter.FILE_PARAMETER_FIELD
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.usecases.remove.RemoveDocumentException
import okhttp3.MultipartBody
import org.slf4j.LoggerFactory
import javax.inject.Inject

class LinShareDocumentDataSource @Inject constructor(
    private val linshareApi: LinshareApi,
    private val networkExecutor: NetworkExecutor,
    private val uploadNetworkRequestHandler: UploadNetworkRequestHandler,
    private val copyNetworkRequestHandler: CopyNetworkRequestHandler
) : DocumentDataSource {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LinShareDocumentDataSource::class.java)
    }

    override suspend fun upload(
        documentRequest: DocumentRequest,
        onTransfer: OnTransfer
    ): Document {
        return networkExecutor.execute(
            networkRequest = { uploadToMySpace(documentRequest, onTransfer) },
            onFailure = { uploadNetworkRequestHandler(it) }
        )
    }

    private suspend fun uploadToMySpace(
        documentRequest: DocumentRequest,
        onTransfer: OnTransfer
    ): Document {
        val fileRequestBody = documentRequest.toMeasureRequestBody(onTransfer)
        return linshareApi.upload(
            file = MultipartBody.Part.createFormData(
                FILE_PARAMETER_FIELD,
                documentRequest.uploadFileName,
                fileRequestBody),
            fileSize = documentRequest.file.length()
        )
    }

    override suspend fun remove(documentId: DocumentId): Document {
        try {
            return linshareApi.removeDocument(documentId.uuid.toString())
        } catch (throwable: Throwable) {
            LOGGER.error("remove() ${throwable.printStackTrace()}")
            throw RemoveDocumentException(throwable)
        }
    }

    override suspend fun getAll(): List<Document> {
        return linshareApi.getAll().sortedByDescending { it.modificationDate }
    }

    override suspend fun search(query: QueryString): List<Document> {
        return getAll()
            .filter { document -> document.nameContains(query.value) }
    }

    override suspend fun share(shareRequest: ShareRequest): List<Share> {
        return linshareApi.share(shareRequest)
    }

    override suspend fun copy(copyRequest: CopyRequest): List<Document> {
        return networkExecutor.execute(
            networkRequest = { linshareApi.copyInMySpace(copyRequest) },
            onFailure = { copyNetworkRequestHandler(it) })
    }
}
