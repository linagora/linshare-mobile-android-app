package com.linagora.android.linshare.domain.model.sharedspace.member

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import java.util.Date

data class SharedSpaceMember(
    @SerializedName("uuid")
    val sharedSpaceMemberId: SharedSpaceMemberId,
    @SerializedName("node")
    val sharedSpaceNode: SharedSpaceNodeNested,
    val role: SharedSpaceRole,
    @SerializedName("account")
    val sharedSpaceAccount: SharedSpaceAccount,
    val creationDate: Date,
    val modificationDate: Date
)
