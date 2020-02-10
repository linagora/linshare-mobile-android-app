package com.linagora.android.linshare.domain.usecases.upload

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.usecases.utils.Success.ViewState

data class UploadSuccessViewState(val document: Document) : ViewState()
data class UploadingViewState(val transferredBytes: TransferredBytes, val totalBytes: TotalBytes) : ViewState()
