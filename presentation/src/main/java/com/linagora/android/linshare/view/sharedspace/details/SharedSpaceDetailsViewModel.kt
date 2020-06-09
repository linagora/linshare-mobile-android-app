package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSingleSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetAllMembersInSharedSpaceInteractor
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedSpaceDetailsViewModel @Inject constructor(
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
    private val getSingleSharedSpaceInteractor: GetSingleSharedSpaceInteractor,
    private val getAllMembersInSharedSpaceInteractor: GetAllMembersInSharedSpaceInteractor
) : BaseViewModel(coroutinesDispatcherProvider) {

    fun getAllMembers(sharedSpaceId: SharedSpaceId) {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            consumeStates(getAllMembersInSharedSpaceInteractor(sharedSpaceId))
        }
    }

    fun getCurrentSharedSpace(sharedSpaceId: SharedSpaceId) {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            consumeStates(getSingleSharedSpaceInteractor(sharedSpaceId))
        }
    }
}
