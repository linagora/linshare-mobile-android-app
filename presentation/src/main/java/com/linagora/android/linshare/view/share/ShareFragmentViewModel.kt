package com.linagora.android.linshare.view.share

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.LinShareApplication
import com.linagora.android.linshare.view.base.LinShareViewModel
import com.linagora.android.linshare.view.share.worker.ShareWorker
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.DOCUMENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.RECIPIENTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.TAG_SHARE_WORKER
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareFragmentViewModel @Inject constructor(
    application: LinShareApplication,
    dispatcherProvider: CoroutinesDispatcherProvider
) : LinShareViewModel(application, dispatcherProvider) {

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
}
