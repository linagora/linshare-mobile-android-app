package com.linagora.android.linshare.domain.model.share

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingListId
import java.util.UUID

data class ShareRequest(
    @SerializedName("mailingListUuid")
    val mailingListIds: Set<MailingListId>,
    val recipients: List<GenericUser>,
    @SerializedName("documents")
    val documentIds: List<UUID>
)
