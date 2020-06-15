package com.linagora.android.linshare.view.base

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole

interface OnSelectRoles {

    fun onSelectRoles(lastSelectedRole: SharedSpaceRole)

    fun onSelectedRoles(selectedRole: SharedSpaceRole)
}
