package com.linagora.android.linshare.view.sharedspacedestination

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceInteractor
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.sharedspace.action.SharedSpaceItemBehavior
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceDestinationViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val getSharedSpaceInteractor: GetSharedSpaceInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(internetAvailable, dispatcherProvider) {

    val sharedSpaceItemBehavior = SharedSpaceItemBehavior(this)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDestinationViewModel::class.java)
    }

    fun onSwipeRefresh() {
        getSharedSpace()
    }

    fun getSharedSpace() {
        LOGGER.info("getSharedSpace()")
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getSharedSpaceInteractor())
        }
    }
}
