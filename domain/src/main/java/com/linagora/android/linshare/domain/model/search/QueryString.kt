package com.linagora.android.linshare.domain.model.search

data class QueryString(val value: String) {
    init {
        require(value.isNotBlank()) { "query is invalid" }
    }
}
