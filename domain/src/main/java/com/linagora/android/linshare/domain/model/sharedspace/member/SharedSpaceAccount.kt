package com.linagora.android.linshare.domain.model.sharedspace.member

import com.google.gson.annotations.SerializedName

data class SharedSpaceAccount(
    @SerializedName("uuid")
    val sharedSpaceAccountId: SharedSpaceAccountId,
    val name: String,
    val firstName: String,
    val lastName: String,
    val mail: String
)
