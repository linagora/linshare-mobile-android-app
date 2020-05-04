package com.linagora.android.linshare.view.share.worker

import android.content.Context
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.usecases.share.ShareDocumentInteractor
import com.linagora.android.linshare.domain.usecases.share.ShareViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.upload.worker.UploadResult
import com.linagora.android.linshare.view.upload.worker.UploadWorker
import com.linagora.android.linshare.view.widget.makeCustomToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.inject.Inject

class ShareWorker @Inject constructor(
    private val appContext: Context,
    private val params: WorkerParameters,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val shareDocumentInteractor: ShareDocumentInteractor,
    private val viewStateStore: ViewStateStore
) : CoroutineWorker(appContext, params) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareWorker::class.java)

        const val TAG_SHARE_WORKER = "shareWorker"

        const val RECIPIENTS_KEY = "recipients"

        const val DOCUMENTS_KEY = "documents"
    }

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.io) {
            try {
                shareDocumentInteractor(extractShareCreation())
                    .collect { state -> consumeShareState(state) }

                getShareResult()
            } catch (throwable: Throwable) {
                LOGGER.error("doWork(): ${throwable.message}")
                alertShareResult(appContext.getString(R.string.share_failed))
                Result.failure()
            }
        }
    }

    private suspend fun consumeShareState(state: State<Either<Failure, Success>>) {
        viewStateStore.storeAndGet(state).fold(
            ifLeft = { alertShareResult(appContext.getString(R.string.share_failed)) },
            ifRight = { success -> reactToSuccessState(success) }
        )
    }

    private suspend fun reactToSuccessState(success: Success) {
        when (success) {
            is ShareViewState -> alertShareResult(appContext.getString(R.string.share_success))
        }
    }

    private fun extractShareCreation(): ShareRequest {
        val recipients = inputData.getStringArray(RECIPIENTS_KEY)
        val documents = inputData.getStringArray(DOCUMENTS_KEY)
        require(recipients!!.isNotEmpty()) { "Can not share without recipient" }
        require(documents!!.isNotEmpty()) { "Can not share without document" }
        return ShareRequest(
            recipients = recipients.map { GenericUser(it) },
            documentIds = documents.map { UUID.fromString(it) }
        )
    }

    private suspend fun alertShareResult(message: String) {
        withContext(dispatcherProvider.main) {
            Toast(appContext).makeCustomToast(appContext, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun getShareResult(): Result {
        return viewStateStore.getCurrentState().fold(
            ifLeft = { getFailureResult() },
            ifRight = this@ShareWorker::getSuccessResult
        )
    }

    private fun getFailureResult(): Result {
        return Result.success(workDataOf(
            UploadWorker.RESULT_MESSAGE to appContext.getString(R.string.upload_success_but_share_failed),
            UploadWorker.UPLOAD_RESULT to UploadResult.UPLOAD_AND_SHARE_FAILED.name
        ))
    }

    private fun getSuccessResult(success: Success): Result {
        return Result.success(workDataOf(
            UploadWorker.RESULT_MESSAGE to getSuccessMessage(success),
            UploadWorker.UPLOAD_RESULT to UploadResult.UPLOAD_AND_SHARE_SUCCESS.name
        ))
    }

    private fun getSuccessMessage(success: Success): String {
        return when (success) {
            is ShareViewState -> appContext.resources
                .getQuantityString(R.plurals.file_is_shared_with_people, success.shares.size, success.shares.size)
            else -> appContext.getString(R.string.upload_and_share_success)
        }
    }

    class Factory @Inject constructor(
        private val dispatcherProvider: CoroutinesDispatcherProvider,
        private val shareDocumentInteractor: ShareDocumentInteractor,
        private val viewStateStore: ViewStateStore
    ) : ChildWorkerFactory {
        override fun create(
            applicationContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return ShareWorker(
                applicationContext,
                params,
                dispatcherProvider,
                shareDocumentInteractor,
                viewStateStore
            )
        }
    }
}
