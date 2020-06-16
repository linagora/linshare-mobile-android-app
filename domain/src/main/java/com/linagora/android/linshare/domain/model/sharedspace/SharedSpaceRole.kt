package com.linagora.android.linshare.domain.model.sharedspace

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceOperationRole.UploadRoles

data class SharedSpaceRole(
    @SerializedName("uuid")
    val sharedSpaceRoleId: SharedSpaceRoleId,
    val name: SharedSpaceRoleName
)

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

    val AddMembersRole = listOf(
        SharedSpaceRoleName.ADMIN
    )
}

fun SharedSpaceRole.canUpload(): Boolean {
    return UploadRoles.contains(this.name)
}
