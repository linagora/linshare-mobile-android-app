package com.linagora.android.linshare.domain.model.autocomplete

data class AutoCompletePattern(val value: String) {
    init {
        require(value.isNotBlank()) { "pattern in invalid" }
    }
}
