package com.linagora.android.linshare.view.upload.worker

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.util.pushSystemNotification
import okhttp3.MediaType.Companion.toMediaType
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadWorker(
    private val appContext: Context,
    private val params: WorkerParameters,
    private val documentRepository: DocumentRepository
) : CoroutineWorker(appContext, params) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadWorker::class.java)

        const val FILE_URI_INPUT_KEY = "upload_file_uri"
    }

    override suspend fun doWork(): Result {
        try {
            val fileUri = Uri.parse(inputData.getString(FILE_URI_INPUT_KEY))
            queryDocumentFromSystemFile(fileUri)!!
                .let { document ->
                    pushSystemNotification(
                        title = appContext.getString(R.string.uploading_title),
                        message = String.format(appContext.getString(R.string.uploading_file), document.fileName),
                        context = appContext
                    )
                    documentRepository.upload(document)
                    pushSystemNotification(
                        title = appContext.getString(R.string.upload_finished_title),
                        message = String.format(appContext.getString(R.string.upload_success), document.fileName),
                        context = appContext
                    )
                }
            return Result.success()
        } catch (exp: Exception) {
            LOGGER.error(exp.message, exp)
            pushSystemNotification(
                title = appContext.getString(R.string.upload_finished_title),
                message = String.format(appContext.getString(R.string.upload_failed)),
                context = appContext
            )
            return Result.failure()
        }
    }

    private fun queryDocumentFromSystemFile(uri: Uri): DocumentRequest? {
        return appContext.contentResolver.query(uri, null, null, null, null)
            ?.use { cursor ->
                with(cursor) {
                    moveToFirst()
                    val fileName = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val size = getLong(getColumnIndex(OpenableColumns.SIZE))
                    val mimeType = getString(getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    DocumentRequest(uri, fileName, size, mimeType.toMediaType())
                }
            }
    }

    class Factory @Inject constructor(
        private val documentRepository: DocumentRepository
    ) : ChildWorkerFactory {
        override fun create(
            applicationContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return UploadWorker(applicationContext, params, documentRepository)
        }
    }
}
