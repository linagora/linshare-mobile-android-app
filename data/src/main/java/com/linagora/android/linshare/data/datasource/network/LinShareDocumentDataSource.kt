package com.linagora.android.linshare.data.datasource.network

import android.content.Context
import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import okhttp3.MultipartBody
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LinShareDocumentDataSource @Inject constructor(
    private val context: Context,
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
        val tempFile = createTempUploadFile(documentRequest)
        try {
            val fileRequestBody = MeasurableUploadRequestBody(
                contentType = documentRequest.mediaType,
                file = tempFile,
                onTransfer = onTransfer
            )
            return linshareApi.upload(
                file = MultipartBody.Part.createFormData(
                    FILE_PARAMETER_FIELD,
                    documentRequest.fileName,
                    fileRequestBody),
                fileSize = tempFile.length()
            )
        } catch (httpExp: HttpException) {
            val errorResponse = parseErrorResponse(httpExp)
            throw UploadException(errorResponse)
        } catch (exp: Exception) {
            LOGGER.error("$exp - ${exp.printStackTrace()}")
            throw UploadException(ErrorResponse.UNKNOWN_RESPONSE)
        } finally {
            FileUtils.deleteQuietly(tempFile)
        }
    }

    override suspend fun getAll(): List<Document> {
        return linshareApi.getAll().sortedByDescending { it.modificationDate }
    }

    private fun createTempUploadFile(documentRequest: DocumentRequest): File {
        val tempFile = File.createTempFile(
            "${documentRequest.fileName}_${System.currentTimeMillis()}",
            ".temp"
        )
        FileOutputStream(tempFile)
            .use { IOUtils.copy(context.contentResolver.openInputStream(documentRequest.uri), it) }
        return tempFile
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
}
