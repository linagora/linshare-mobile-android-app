package com.linagora.android.linshare.view.sharedspace

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceInteractor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.sharedspace.action.SharedSpaceItemBehavior
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedSpaceViewModel @Inject constructor(
    private val getSharedSpaceInteractor: GetSharedSpaceInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    val sharedSpaceItemBehavior = SharedSpaceItemBehavior(this)

    fun onSwipeRefresh() {
        getSharedSpace()
    }

    fun getSharedSpace() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getSharedSpaceInteractor())
        }
    }
}
