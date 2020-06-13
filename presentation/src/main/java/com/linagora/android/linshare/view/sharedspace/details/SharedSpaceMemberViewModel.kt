package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetAllMembersInSharedSpaceInteractor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedSpaceMemberViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getAllMembersInSharedSpaceInteractor: GetAllMembersInSharedSpaceInteractor
) : BaseViewModel(dispatcherProvider) {

    fun getAllMembers(sharedSpaceId: SharedSpaceId) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAllMembersInSharedSpaceInteractor(sharedSpaceId))
        }
    }
}
