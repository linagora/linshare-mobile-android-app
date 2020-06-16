package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.viewModelScope
import com.linagora.android.linshare.domain.model.sharedspace.member.AddMemberRequest
import com.linagora.android.linshare.domain.usecases.sharedspace.member.AddMember
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllRoles
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.action.OnSelectRolesBehavior
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.widget.AddMemberSuggestionManager
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedSpaceAddMemberViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val getAllRoles: GetAllRoles,
    private val addMember: AddMember,
    val addMemberSuggestionManager: AddMemberSuggestionManager
) : BaseViewModel(dispatcherProvider) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceAddMemberViewModel::class.java)
    }

    val onSelectRoleBehavior = OnSelectRolesBehavior(this)

    fun getSharedSpaceRoles() {
        LOGGER.info("getSharedSpaceRoles()")
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(getAllRoles())
        }
    }

    fun addMemberToSharedSpace(addMemberRequest: AddMemberRequest) {
        viewModelScope.launch(dispatcherProvider.io) {
            consumeStates(addMember(addMemberRequest))
        }
    }
}
