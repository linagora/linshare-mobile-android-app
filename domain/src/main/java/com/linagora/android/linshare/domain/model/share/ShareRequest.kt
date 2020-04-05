package com.linagora.android.linshare.domain.model.share

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.GenericUser
import java.util.UUID

data class ShareRequest(
    val recipients: List<GenericUser>,
    @SerializedName("documents")
    val documentIds: List<UUID>
)
