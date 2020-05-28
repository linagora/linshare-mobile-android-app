package com.linagora.android.linshare.view.upload.request

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.util.append
import com.linagora.android.linshare.view.share.worker.ShareWorker
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.MAILING_LISTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.RECIPIENTS_KEY
import com.linagora.android.linshare.view.upload.worker.UploadCompletedNotificationWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_REQUEST_TYPE

class UploadAndShareRequest constructor(
    private val workManager: WorkManager,
    private val recipients: Set<GenericUser>,
    private val mailingLists: Set<MailingList>
) : UploadWorkerRequest {

    override fun execute(inputData: Data) {
        val data = inputData.append(workDataOf(
            UPLOAD_REQUEST_TYPE to UploadRequestType.UploadAndShare.name,
            RECIPIENTS_KEY to recipients.map { it.mail }.toTypedArray(),
            MAILING_LISTS_KEY to mailingLists.map { mailingList -> mailingList.mailingListId }
                .map { it.uuid.toString() }
                .toTypedArray()
        ))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .addTag(UploadWorker.TAG_UPLOAD_WORKER)
            .build()

        val shareWorker = OneTimeWorkRequestBuilder<ShareWorker>()
            .setConstraints(constraints)
            .addTag(ShareWorker.TAG_SHARE_WORKER)
            .build()

        val uploadCompletedNotification = OneTimeWorkRequestBuilder<UploadCompletedNotificationWorker>()
            .build()

        workManager.beginWith(uploadRequest)
            .then(shareWorker)
            .then(uploadCompletedNotification)
            .enqueue()
    }
}
