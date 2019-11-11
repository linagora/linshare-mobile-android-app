package com.linagora.android.linshare.domain.model

import java.util.UUID

data class Token(
    val uuid: UUID,
    val token: String
) {
    init {
        require(token.isNotBlank()) { "token is invalid" }
    }

    fun asBearerHeader() = "Bearer $token"
}
