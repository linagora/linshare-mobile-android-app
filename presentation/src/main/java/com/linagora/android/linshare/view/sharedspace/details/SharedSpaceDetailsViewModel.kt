package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSingleSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.OpenAddMembers
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceDetailsViewModel @Inject constructor(
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
    private val getSingleSharedSpaceInteractor: GetSingleSharedSpaceInteractor
) : BaseViewModel(coroutinesDispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDetailsViewModel::class.java)
    }

    fun getCurrentSharedSpace(sharedSpaceId: SharedSpaceId) {
        LOGGER.info("getCurrentSharedSpace(): $sharedSpaceId")
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            consumeStates(getSingleSharedSpaceInteractor(sharedSpaceId))
        }
    }

    fun onAddMembersButtonClick(sharedSpaceId: SharedSpaceId) {
        dispatchUIState(Either.right(OpenAddMembers(sharedSpaceId)))
    }
}
