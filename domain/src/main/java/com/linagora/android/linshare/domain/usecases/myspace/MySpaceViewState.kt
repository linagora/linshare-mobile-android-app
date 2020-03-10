package com.linagora.android.linshare.domain.usecases.myspace

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class MySpaceViewState(val documents: List<Document>) : Success.ViewState()
data class MySpaceFailure(val throwable: Throwable) : FeatureFailure()
data class ContextMenuClick(val document: Document) : Success.ViewEvent()
data class DownloadClick(val document: Document) : Success.ViewEvent()
object UploadButtonBottomBarClick : Success.ViewEvent()
data class RemoveDocumentSuccessViewState(val document: Document) : Success.ViewState()
data class RemoveDocumentFailure(val throwable: Throwable) : FeatureFailure()
