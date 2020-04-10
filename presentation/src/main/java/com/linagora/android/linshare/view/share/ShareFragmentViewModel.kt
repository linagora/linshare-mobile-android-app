package com.linagora.android.linshare.view.share

import androidx.lifecycle.asLiveData
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.autocomplete.GetAutoCompleteSharingInteractor
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
    dispatcherProvider: CoroutinesDispatcherProvider,
    private val getAutoCompleteSharingInteractor: GetAutoCompleteSharingInteractor
) : LinShareViewModel(application, dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareFragmentViewModel::class.java)
    }

    val queryChannel = BroadcastChannel<AutoCompletePattern>(Channel.CONFLATED)

    private val queryState = queryChannel.asFlow()
        .debounce(QUERY_INTERVAL_MS)
        .flatMapLatest { getAutoCompleteSharingInteractor(it) }

    val suggessions = queryState.asLiveData()

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

    fun onSelectedUserClick(userAutoCompleteResult: UserAutoCompleteResult) {
        LOGGER.info("onSelectedUserClick() $userAutoCompleteResult")
    }
}
