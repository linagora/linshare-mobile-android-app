package com.linagora.android.linshare.view.myspace

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.usecases.myspace.GetAllDocumentsInteractor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MySpaceViewModel @Inject constructor(
    private val getAllDocumentsInteractor: GetAllDocumentsInteractor,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    fun getAllDocuments() {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAllDocumentsInteractor())
        }
    }

    fun onSwipeRefresh() {
        getAllDocuments()
    }
}
