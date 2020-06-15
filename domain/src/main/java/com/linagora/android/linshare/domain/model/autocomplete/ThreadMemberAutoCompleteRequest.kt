package com.linagora.android.linshare.domain.model.autocomplete

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId

data class ThreadMemberAutoCompleteRequest(
    val autoCompletePattern: AutoCompletePattern,
    val autoCompleteType: AutoCompleteType,
    val threadId: SharedSpaceId
)
