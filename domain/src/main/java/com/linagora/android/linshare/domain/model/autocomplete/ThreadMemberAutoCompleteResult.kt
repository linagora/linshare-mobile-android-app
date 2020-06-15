package com.linagora.android.linshare.domain.model.autocomplete

import java.util.UUID

data class ThreadMemberAutoCompleteResult(
    override val identifier: String,
    override val display: String,
    private val userUuid: UUID,
    private val threadUuid: UUID,
    val firstName: String,
    val lastName: String,
    val domain: UUID,
    val mail: String,
    val isMember: Boolean
) : AutoCompleteResult
