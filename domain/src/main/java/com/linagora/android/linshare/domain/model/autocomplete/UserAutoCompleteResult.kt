package com.linagora.android.linshare.domain.model.autocomplete

import java.util.UUID

data class UserAutoCompleteResult(
    override val identifier: UUID,
    override val display: String,
    val firstName: String,
    val lastName: String,
    val domain: UUID,
    val mail: String
) : AutoCompleteResult(identifier, display)
