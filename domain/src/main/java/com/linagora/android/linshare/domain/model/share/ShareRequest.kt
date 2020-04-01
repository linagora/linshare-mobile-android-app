package com.linagora.android.linshare.domain.model.share

import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.document.DocumentId

data class ShareRequest(
    val recipients: List<GenericUser>,
    val documents: List<DocumentId>
)
