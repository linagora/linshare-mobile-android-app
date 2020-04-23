package com.linagora.android.linshare.view.upload.request

import androidx.work.Data

interface UploadWorkerRequest {
    fun execute(inputData: Data)
}
