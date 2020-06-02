package com.linagora.android.linshare.domain.model

data class GenericUser(
    val mail: String,
    val lastName: String? = null,
    val firstName: String? = null
) {
    init {
        require(mail.isNotBlank()) { "mail of generic User must not be empty" }
    }
}

fun GenericUser.fullName(): String? {
    return firstName?.takeIf { it.isNotBlank() }
        ?.let { firstName -> lastName
            ?.let { "$firstName $it" }
            ?: firstName }
}
