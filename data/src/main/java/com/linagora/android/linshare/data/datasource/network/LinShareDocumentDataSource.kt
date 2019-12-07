package com.linagora.android.linshare.data.datasource.network

import android.content.Context
import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LinShareDocumentDataSource @Inject constructor(
    private val context: Context,
    private val linshareApi: LinshareApi
) : DocumentDataSource {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LinShareDocumentDataSource::class.java)
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
                file = fileRequestBody,
                fileName = documentRequest.fileName,
                fileSize = documentRequest.fileSize
            )
        } catch (exp: Exception) {
            LOGGER.error("${exp.message} - ${exp.printStackTrace()}")
            throw UploadException(exp.message)
        } finally {
            FileUtils.deleteQuietly(tempFile)
        }
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
}
