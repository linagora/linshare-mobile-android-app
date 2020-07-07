/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

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
import com.linagora.android.linshare.domain.model.autocomplete.MailingListId
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

        const val MAILING_LISTS_KEY = "mailingLists"
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
        val mailingListsArg = inputData.getStringArray(MAILING_LISTS_KEY)
        val recipientsArg = inputData.getStringArray(RECIPIENTS_KEY)
        val documents = inputData.getStringArray(DOCUMENTS_KEY)

        validateShareReceiverArgument(mailingListsArg, recipientsArg)
        require(documents!!.isNotEmpty()) { "Can not share without document" }

        val mailingListIds = mailingListsArg
            ?.map { MailingListId(UUID.fromString(it)) }
            ?.toSet()
            ?: emptySet()

        val recipients = recipientsArg
            ?.map { GenericUser(it) }
            ?: emptyList()

        return ShareRequest(
            mailingListIds = mailingListIds,
            recipients = recipients,
            documentIds = documents.map { UUID.fromString(it) }
        )
    }

    private fun validateShareReceiverArgument(
        mailingLists: Array<String>?,
        recipients: Array<String>?
    ) {
        require(!(recipients.isNullOrEmpty() && mailingLists.isNullOrEmpty())) {
            "Can not share without receivers"
        }
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
