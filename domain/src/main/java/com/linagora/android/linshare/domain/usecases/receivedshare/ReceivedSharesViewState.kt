package com.linagora.android.linshare.domain.usecases.receivedshare

import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class ReceivedSharesViewState(val receivedList: List<Share>) : Success.ViewState()
data class ReceivedSharesFailure(val throwable: Throwable) : FeatureFailure()
data class ContextMenuReceivedShareClick(val share: Share) : Success.ViewEvent()
data class DownloadReceivedShareClick(val share: Share) : Success.ViewEvent()
