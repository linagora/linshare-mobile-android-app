package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.document.nameContains
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.usecases.remove.RemoveDocumentException
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import okhttp3.MultipartBody
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import java.net.SocketException
import javax.inject.Inject

class LinShareDocumentDataSource @Inject constructor(
    private val retrofit: Retrofit,
    private val linshareApi: LinshareApi
) : DocumentDataSource {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LinShareDocumentDataSource::class.java)

        private const val FILE_PARAMETER_FIELD = "file"
    }

    override suspend fun upload(
        documentRequest: DocumentRequest,
        onTransfer: OnTransfer
    ): Document {
        try {
            val fileRequestBody = MeasurableUploadRequestBody(
                contentType = documentRequest.mediaType,
                file = documentRequest.file,
                onTransfer = onTransfer
            )
            return linshareApi.upload(
                file = MultipartBody.Part.createFormData(
                    FILE_PARAMETER_FIELD,
                    documentRequest.uploadFileName,
                    fileRequestBody),
                fileSize = documentRequest.file.length()
            )
        } catch (httpExp: HttpException) {
            val errorResponse = parseErrorResponse(httpExp)
            throw UploadException(errorResponse)
        } catch (exp: Exception) {
            LOGGER.error("$exp - ${exp.printStackTrace()}")
            when (exp) {
                is SocketException -> throw UploadException(ErrorResponse.INTERNET_NOT_AVAILABLE)
                else -> throw UploadException(ErrorResponse.UNKNOWN_RESPONSE)
            }
        }
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

    private fun parseErrorResponse(httpException: HttpException): ErrorResponse {
        return runCatching {
            httpException.response()
                ?.errorBody()
                .let {
                    val converter = retrofit.responseBodyConverter<ErrorResponse>(
                        ErrorResponse::class.java,
                        arrayOfNulls<Annotation>(0))
                    converter.convert(it) ?: ErrorResponse.UNKNOWN_RESPONSE
                }
        }.getOrElse {
            LOGGER.error("parseErrorResponse: ${it.printStackTrace()}")
            ErrorResponse.UNKNOWN_RESPONSE
        }
    }

    override suspend fun search(query: String): List<Document> {
        return getAll()
            .filter { document -> document.nameContains(query) }
    }
}
