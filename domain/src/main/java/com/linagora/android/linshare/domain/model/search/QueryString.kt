package com.linagora.android.linshare.domain.model.search

data class QueryString(val value: String) {

    fun getLength() = value.length
}
