package com.linagora.android.linshare.view.share

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import arrow.core.Either
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.share.AddMailingList
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.ShareButtonClick
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.LinShareApplication
import com.linagora.android.linshare.view.base.LinShareViewModel
import com.linagora.android.linshare.view.share.worker.ShareWorker
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.DOCUMENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.MAILING_LISTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.RECIPIENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.TAG_SHARE_WORKER
import com.linagora.android.linshare.view.widget.ShareRecipientsManager
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareFragmentViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    application: LinShareApplication,
    dispatcherProvider: CoroutinesDispatcherProvider,
    val recipientsManager: ShareRecipientsManager
) : LinShareViewModel(internetAvailable, application, dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareFragmentViewModel::class.java)
    }

    fun share(document: Document) {
        val mailingLists = recipientsManager.mailingLists.value
            ?.let { it.toList() }
            ?: emptyList()

        val recipients = recipientsManager.recipients.value
            ?.let { it.toList() }
            ?: emptyList()

        val inputData = createShareInputData(
            mailingLists = mailingLists,
            recipients = recipients,
            document = document
        )

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

    private fun createShareInputData(
        mailingLists: List<MailingList>,
        recipients: List<GenericUser>,
        document: Document
    ): Data {
        return workDataOf(
            MAILING_LISTS_KEY to mailingLists.map { mailingList -> mailingList.mailingListId }
                .map { it.uuid.toString() }
                .toTypedArray(),
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

    fun addMailingList(mailingList: MailingList) {
        LOGGER.info("addMailingList(): $mailingList")
        if (recipientsManager.addMailingList(mailingList)) {
            dispatchState(Either.right(AddMailingList(mailingList)))
        }
    }

    fun removeMailingList(mailingList: MailingList) {
        LOGGER.info("removeMailingList(): $mailingList")
        recipientsManager.removeMailingList(mailingList)
    }

    fun onShareClick(document: Document) {
        share(document)
        dispatchState(Either.right(ShareButtonClick))
    }

    fun resetRecipientManager() = recipientsManager.resetShareRecipientManager()
}
