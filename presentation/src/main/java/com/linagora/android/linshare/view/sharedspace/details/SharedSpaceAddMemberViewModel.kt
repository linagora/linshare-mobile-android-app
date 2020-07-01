package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.member.AddMemberRequest
import com.linagora.android.linshare.domain.usecases.sharedspace.member.AddMember
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetAllMembersInSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllRoles
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.action.OnSelectRolesBehavior
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.widget.AddMemberSuggestionManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceAddMemberViewModel @Inject constructor(
    override val internetAvailable: ConnectionLiveData,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getAllRoles: GetAllRoles,
    private val addMember: AddMember,
    private val getAllMembersInSharedSpace: GetAllMembersInSharedSpaceInteractor,
    val addMemberSuggestionManager: AddMemberSuggestionManager
) : BaseViewModel(internetAvailable, dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceAddMemberViewModel::class.java)
    }

    val onSelectRoleBehavior = OnSelectRolesBehavior(this)

    fun initData(sharedSpaceId: SharedSpaceId) {
        LOGGER.info("initData(): $sharedSpaceId")
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(
                getAllRoles().onCompletion {
                    LOGGER.info("initData(): onCompletion $sharedSpaceId")
                    getAllMembersInSharedSpace(sharedSpaceId)
                        .collect { emit(it) }
                })
        }
    }

    fun addMemberToSharedSpace(addMemberRequest: AddMemberRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(addMember(addMemberRequest))
        }
    }

    fun getAllMembers(sharedSpaceId: SharedSpaceId) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAllMembersInSharedSpace(sharedSpaceId))
        }
    }
}
