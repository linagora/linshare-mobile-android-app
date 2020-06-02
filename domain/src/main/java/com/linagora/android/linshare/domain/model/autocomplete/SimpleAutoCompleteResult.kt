package com.linagora.android.linshare.domain.model.autocomplete

import com.linagora.android.linshare.domain.model.GenericUser

data class SimpleAutoCompleteResult(
    override val identifier: String,
    override val display: String
) : AutoCompleteResult

fun SimpleAutoCompleteResult.toGenericUser(): GenericUser {
    return GenericUser(mail = identifier, firstName = display)
}
