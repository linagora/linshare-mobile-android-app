package com.linagora.android.linshare.util.binding

import com.linagora.android.linshare.databinding.AddMembersViewBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllSharedSpaceRolesSuccess
import com.linagora.android.linshare.domain.usecases.utils.Success

fun AddMembersViewBinding.bindingRoles(success: Success) {
    if (success is GetAllSharedSpaceRolesSuccess) {
        sharedSpaceRoles = success.roles
    }
}

fun AddMembersViewBinding.bindingDefaultSelectedRole(success: Success) {
    if (selectedRole == null && success is GetAllSharedSpaceRolesSuccess) {
        selectedRole = success.roles.firstOrNull { role -> role.name == SharedSpaceRoleName.READER }
    }
}
