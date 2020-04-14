package com.linagora.android.linshare.view.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import arrow.core.Either
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.autocomplete.GetAutoCompleteSharingInteractor
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.ShareButtonClick
import com.linagora.android.linshare.util.Constant.QUERY_INTERVAL_MS
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.LinShareApplication
import com.linagora.android.linshare.view.base.LinShareViewModel
import com.linagora.android.linshare.view.share.worker.ShareWorker
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.DOCUMENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.RECIPIENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.TAG_SHARE_WORKER
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareFragmentViewModel @Inject constructor(
    application: LinShareApplication,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getAutoCompleteSharingInteractor: GetAutoCompleteSharingInteractor
) : LinShareViewModel(application, dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareFragmentViewModel::class.java)
    }

    val queryChannel = BroadcastChannel<AutoCompletePattern>(Channel.CONFLATED)

    private val queryState = queryChannel.asFlow()
        .debounce(QUERY_INTERVAL_MS)
        .flatMapLatest { getAutoCompleteSharingInteractor(it) }

    val suggestions = queryState.asLiveData()

    private val mutableRecipients = MutableLiveData<Set<GenericUser>>()
        .apply { value = HashSet() }

    val recipients: LiveData<Set<GenericUser>> = mutableRecipients

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
        if (mutableRecipients.value?.contains(user) == true) {
            return
        }

        val newRecipients = mutableRecipients.value
            ?.let { mutableSetOf(user).plus(it) }

        mutableRecipients.value = newRecipients
        dispatchUIState(Either.right(AddRecipient(user)))
    }

    fun removeRecipient(user: GenericUser) {
        LOGGER.info("removeRecipient() $user")
        mutableRecipients.value?.flatMap { mutableSetOf(it) }
            ?.minus(user)
            ?.toCollection(LinkedHashSet())
            ?.also { mutableRecipients.value = it }
    }

    fun onShareClick(document: Document) {
        share(recipients.value!!.toList(), document)
        dispatchUIState(Either.right(ShareButtonClick))
    }

    fun resetRecipients() {
        mutableRecipients.value = mutableSetOf()
    }
}
