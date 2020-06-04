package com.linagora.android.linshare.view.upload

import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.upload.UploadDocumentRequest

data class ExtractInfoSuccess(val uploadDocumentRequest: UploadDocumentRequest) : Success.ViewState()
data class OnUploadButtonClick(val uploadDocumentRequest: UploadDocumentRequest) : Success.ViewEvent()
data class BuildDocumentRequestSuccess(val documentRequest: DocumentRequest) : Success.ViewState()
