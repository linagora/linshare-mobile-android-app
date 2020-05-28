package com.linagora.android.linshare.domain.model.autocomplete

import android.util.Patterns

private const val CHARACTER_MAIL = "@"

data class AutoCompletePattern(val value: String) {
    init {
        require(value.isNotBlank()) { "pattern in invalid" }
    }
}

fun AutoCompletePattern.isEmailValid(): Boolean {
    return this.takeIf { this.value.contains(CHARACTER_MAIL) }
        ?.let { Patterns.EMAIL_ADDRESS.matcher(this.value).matches() }
        ?: false
}

fun AutoCompletePattern.toExternalUser(): UserAutoCompleteResult {
    return UserAutoCompleteResult(
        identifier = value,
        display = value,
        firstName = null,
        lastName = null,
        domain = null,
        mail = value)
}
