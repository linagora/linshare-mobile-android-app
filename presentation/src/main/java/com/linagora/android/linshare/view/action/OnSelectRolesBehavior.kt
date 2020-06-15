package com.linagora.android.linshare.view.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectRoleClick
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectedRole
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.OnSelectRoles

class OnSelectRolesBehavior(val viewModel: BaseViewModel) : OnSelectRoles {
    override fun onSelectRoles(lastSelectedRole: SharedSpaceRole) {
        viewModel.dispatchUIState(Either.right(OnSelectRoleClick(lastSelectedRole)))
    }

    override fun onSelectedRoles(selectedRole: SharedSpaceRole) {
        viewModel.dispatchUIState(Either.right(OnSelectedRole(selectedRole)))
    }
}
