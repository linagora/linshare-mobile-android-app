package com.linagora.android.linshare.domain.model.sharedspace

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceOperationRole.UploadRoles
import java.util.UUID

data class SharedSpaceRole(val uuid: UUID, val name: SharedSpaceRoleName)

object SharedSpaceOperationRole {
    val UploadRoles = listOf(
        SharedSpaceRoleName.CONTRIBUTOR,
        SharedSpaceRoleName.WRITER,
        SharedSpaceRoleName.ADMIN
    )

    val DeleteRoles = listOf(
        SharedSpaceRoleName.WRITER,
        SharedSpaceRoleName.ADMIN
    )
}

fun SharedSpaceRole.canUpload(): Boolean {
    return UploadRoles.contains(this.name)
}
