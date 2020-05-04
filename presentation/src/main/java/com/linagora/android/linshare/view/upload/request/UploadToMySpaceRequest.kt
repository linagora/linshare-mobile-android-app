package com.linagora.android.linshare.view.upload.request

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.linagora.android.linshare.util.append
import com.linagora.android.linshare.view.upload.worker.UploadCompletedNotificationWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker

class UploadToMySpaceRequest constructor(private val workManager: WorkManager) : UploadWorkerRequest {

    override fun execute(inputData: Data) {
        val data = inputData.append(workDataOf(
            UploadWorker.UPLOAD_REQUEST_TYPE to UploadRequestType.UploadToMySpace.name
        ))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .addTag(UploadWorker.TAG_UPLOAD_WORKER)
            .build()

        val uploadCompletedNotification = OneTimeWorkRequestBuilder<UploadCompletedNotificationWorker>()
            .build()

        workManager.beginWith(uploadRequest)
            .then(uploadCompletedNotification)
            .enqueue()
    }
}
