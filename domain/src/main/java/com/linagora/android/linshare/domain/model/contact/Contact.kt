package com.linagora.android.linshare.domain.model.contact

import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.SimpleAutoCompleteResult

interface Contact {
    val displayName: String
    val email: String
}

fun Contact.toAutoCompleteResult(): AutoCompleteResult {
    return SimpleAutoCompleteResult(email, displayName)
}
