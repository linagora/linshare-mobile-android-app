package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetAllMembersInSharedSpaceInteractor
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedSpaceMemberViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getAllMembersInSharedSpaceInteractor: GetAllMembersInSharedSpaceInteractor
) : BaseViewModel(internetAvailable, dispatcherProvider) {

    fun getAllMembers(sharedSpaceId: SharedSpaceId) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAllMembersInSharedSpaceInteractor(sharedSpaceId))
        }
    }
}
