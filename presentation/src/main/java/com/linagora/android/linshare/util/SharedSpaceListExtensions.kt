package com.linagora.android.linshare.util

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName

fun List<SharedSpaceNodeNested>.filterSharedSpaceDestinationByRole(operationRoles: List<SharedSpaceRoleName>): List<SharedSpaceNodeNested> {
    return operationRoles.takeIf { it.isNotEmpty() }
        ?.let { this.filter { sharedSpace -> operationRoles.contains(sharedSpace.role.name) } }
        ?: this
}
