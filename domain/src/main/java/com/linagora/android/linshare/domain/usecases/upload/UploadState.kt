package com.linagora.android.linshare.domain.usecases.upload

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.ViewState

data class UploadSuccessViewState(val document: Document) : ViewState()
data class UploadingViewState(val transferredBytes: TransferredBytes, val totalBytes: TotalBytes) : ViewState()
object PreUploadError : Failure.FeatureFailure()
data class UploadSuccess(val message: String) : Success.ViewState()
data class UploadFailed(val message: String) : Failure.FeatureFailure()
