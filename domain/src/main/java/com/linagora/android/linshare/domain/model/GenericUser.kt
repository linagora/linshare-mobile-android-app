package com.linagora.android.linshare.domain.model

data class GenericUser(val mail: String) {
    init {
        require(mail.isNotBlank()) { "mail of generic User must not be empty" }
    }
}
