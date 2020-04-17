package com.linagora.android.linshare.view.receivedshares

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.adapter.receivedshares.action.ReceivedShareDownloadContextMenu
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.receivedshare.ContextMenuReceivedShareClick
import com.linagora.android.linshare.domain.usecases.receivedshare.GetReceivedSharesInteractor
import com.linagora.android.linshare.operator.download.DownloadOperator
import com.linagora.android.linshare.operator.download.toDownloadRequest
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ListItemBehavior
import com.linagora.android.linshare.view.myspace.MySpaceViewModel.Companion.NO_DOWNLOADING_DOCUMENT
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceivedSharesViewModel @Inject constructor(
    private val getReceivedSharesInteractor: GetReceivedSharesInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val downloadOperator: DownloadOperator
) : BaseViewModel(dispatcherProvider),
    ListItemBehavior<Share> {

    val downloadContextMenu = ReceivedShareDownloadContextMenu(this)

    override fun onContextMenuClick(data: Share) {
        dispatchState(Either.right(ContextMenuReceivedShareClick(data)))
    }

    fun getReceivedList() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getReceivedSharesInteractor())
        }
    }

    fun getDownloading(): Share? {
        return downloadContextMenu.downloadingData.get()
    }

    fun downloadShare(credential: com.linagora.android.linshare.domain.model.Credential, token: Token, share: Share) {
        viewModelScope.launch(dispatcherProvider.io) {
            downloadContextMenu.setDownloading(NO_DOWNLOADING_DOCUMENT)
            downloadOperator.download(credential, token, share.toDownloadRequest())
        }
    }

    fun onSwipeRefresh() {
        getReceivedList()
    }
}
