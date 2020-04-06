package com.linagora.android.linshare.view.receivedshares

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.receivedshare.GetReceivedSharesInteractor
import arrow.core.Either
import com.linagora.android.linshare.adapter.receivedshares.action.ReceivedSharePersonalContextMenu
import com.linagora.android.linshare.domain.usecases.receivedshare.ContextMenuReceivedShareClick
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

    val personalItemContextMenu = ReceivedSharePersonalContextMenu(this)

    override fun onContextMenuClick(data: Share) {
        dispatchState(Either.right(ContextMenuReceivedShareClick(data)))
    }

    fun getReceivedList() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getReceivedSharesInteractor())
        }
    }

    fun onSwipeRefresh() {
        getReceivedList()
    }
}
