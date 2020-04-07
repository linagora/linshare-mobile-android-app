package com.linagora.android.linshare.view.receivedshares

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.receivedshare.GetReceivedSharesInteractor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ListItemBehavior
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceivedSharesViewModel @Inject constructor(
    private val getReceivedSharesInteractor: GetReceivedSharesInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider),
    ListItemBehavior<Share> {

    override fun onContextMenuClick(data: Share) {}

    fun getReceivedList() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getReceivedSharesInteractor())
        }
    }

    fun onSwipeRefresh() {
        getReceivedList()
    }
}
