package com.linagora.android.linshare.view.share.worker

import android.content.Context
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.usecases.share.ShareDocumentInteractor
import com.linagora.android.linshare.domain.usecases.share.ShareViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel.Companion.INITIAL_STATE
import com.linagora.android.linshare.view.widget.makeCustomToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ShareWorker @Inject constructor(
    private val appContext: Context,
    private val params: WorkerParameters,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val shareDocumentInteractor: ShareDocumentInteractor
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TAG_SHARE_WORKER = "shareWorker"

        const val RECIPIENTS_KEY = "recipients"

        const val DOCUMENTS_KEY = "documents"
    }

    override suspend fun doWork(): Result {
        try {
            withContext(dispatcherProvider.io) {
                shareDocumentInteractor(extractShareCreation())
                    .collect { state -> consumeShareState(state) }
            }
        } catch (throwable: Throwable) {
            alertShareResult(appContext.getString(R.string.share_failed))
        }
        return Result.success()
    }

    private suspend fun consumeShareState(state: State<Either<Failure, Success>>) {
        state(INITIAL_STATE).fold(
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
        require(recipients!!.isNotEmpty())
        require(documents!!.isNotEmpty())
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

    class Factory @Inject constructor(
        private val dispatcherProvider: CoroutinesDispatcherProvider,
        private val shareDocumentInteractor: ShareDocumentInteractor
    ) : ChildWorkerFactory {
        override fun create(
            applicationContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return ShareWorker(
                applicationContext,
                params,
                dispatcherProvider,
                shareDocumentInteractor
            )
        }
    }
}
