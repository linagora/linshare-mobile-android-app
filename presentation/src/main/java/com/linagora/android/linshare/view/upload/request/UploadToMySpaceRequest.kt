package com.linagora.android.linshare.view.upload.request

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.linagora.android.linshare.view.upload.worker.UploadWorker

class UploadToMySpaceRequest constructor(private val workManager: WorkManager) : UploadWorkerRequest {

    override fun execute(inputData: Data) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(UploadWorker.TAG_UPLOAD_WORKER)
            .build()

        workManager.enqueue(uploadRequest)
    }
}
