package com.linagora.android.linshare.domain.model.autocomplete

import com.linagora.android.linshare.domain.model.GenericUser
import java.util.UUID

data class UserAutoCompleteResult(
    override val identifier: String,
    override val display: String,
    val firstName: String?,
    val lastName: String?,
    val domain: UUID?,
    val mail: String?
) : AutoCompleteResult

fun UserAutoCompleteResult.fullName(): String? {
    return firstName?.takeIf { it.isNotBlank() }
        ?.let { "$it $lastName" }
}

fun UserAutoCompleteResult.toGenericUser(): GenericUser {
    return GenericUser(
        mail = mail ?: display,
        lastName = lastName,
        firstName = firstName
    )
}
