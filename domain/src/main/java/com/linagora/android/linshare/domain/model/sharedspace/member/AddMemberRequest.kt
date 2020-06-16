package com.linagora.android.linshare.domain.model.sharedspace.member

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleId

data class AddMemberRequest(
    @SerializedName("account")
    val sharedSpaceAccountId: SharedSpaceAccountId,
    @SerializedName("node")
    val sharedSpaceId: SharedSpaceId,
    @SerializedName("role")
    val sharedSpaceRoleId: SharedSpaceRoleId
)
