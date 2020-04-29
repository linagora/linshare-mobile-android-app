package com.linagora.android.linshare.view.share

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import arrow.core.Either
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.ShareButtonClick
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.LinShareApplication
import com.linagora.android.linshare.view.base.LinShareViewModel
import com.linagora.android.linshare.view.share.worker.ShareWorker
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.DOCUMENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.RECIPIENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.TAG_SHARE_WORKER
import com.linagora.android.linshare.view.widget.ShareRecipientsManager
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareFragmentViewModel @Inject constructor(
    application: LinShareApplication,
    dispatcherProvider: CoroutinesDispatcherProvider,
    val recipientsManager: ShareRecipientsManager
) : LinShareViewModel(application, dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareFragmentViewModel::class.java)
    }

    fun share(recipients: List<GenericUser>, document: Document) {
        val inputData = createShareInputData(recipients, document)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val shareWorker = OneTimeWorkRequestBuilder<ShareWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(TAG_SHARE_WORKER)
            .build()

        WorkManager.getInstance(application.applicationContext).enqueue(shareWorker)
    }

    private fun createShareInputData(recipients: List<GenericUser>, document: Document): Data {
        return workDataOf(
            RECIPIENTS_KEY to recipients.map { user -> user.mail }.toTypedArray(),
            DOCUMENTS_KEY to listOf(document.documentId.uuid.toString()).toTypedArray()
        )
    }

    fun addRecipient(user: GenericUser) {
        LOGGER.info("addRecipient() $user")
        if (recipientsManager.addRecipient(user)) {
            dispatchState(Either.right(AddRecipient(user)))
        }
    }

    fun removeRecipient(user: GenericUser) {
        LOGGER.info("removeRecipient() $user")
        recipientsManager.removeRecipient(user)
    }

    fun onShareClick(document: Document) {
        share(recipientsManager.recipients.value!!.toList(), document)
        dispatchState(Either.right(ShareButtonClick))
    }

    fun resetRecipients() = recipientsManager.resetRecipients()
}
