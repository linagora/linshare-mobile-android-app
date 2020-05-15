package com.linagora.android.linshare.view.upload.request

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.util.append
import com.linagora.android.linshare.view.upload.worker.UploadCompletedNotificationWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker
import org.slf4j.LoggerFactory

class UploadToSharedSpaceRequest constructor(
    private val workManager: WorkManager,
    private val sharedSpaceId: SharedSpaceId,
    private val sharedSpaceQuotaId: QuotaId,
    private val parentNodeId: WorkGroupNodeId
) : UploadWorkerRequest {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadToSharedSpaceRequest::class.java)
    }

    override fun execute(inputData: Data) {
        LOGGER.info("execute()")
        val data = appendInputData(inputData)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        val uploadCompletedNotification = OneTimeWorkRequestBuilder<UploadCompletedNotificationWorker>()
            .build()

        workManager.beginWith(uploadRequest)
            .then(uploadCompletedNotification)
            .enqueue()
    }

    private fun appendInputData(data: Data): Data {
        return data.append(workDataOf(
            UploadWorker.UPLOAD_REQUEST_TYPE to UploadRequestType.UploadToSharedSpace.name,
            UploadWorker.UPLOAD_TO_SHARED_SPACE_ID_KEY to sharedSpaceId.uuid.toString(),
            UploadWorker.UPLOAD_TO_SHARED_SPACE_QUOTA_ID_KEY to sharedSpaceQuotaId.uuid.toString(),
            UploadWorker.UPLOAD_TO_PARENT_NODE_ID_KEY to parentNodeId.uuid.toString()
        ))
    }
}
