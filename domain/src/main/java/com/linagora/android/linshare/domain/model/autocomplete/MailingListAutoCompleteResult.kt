package com.linagora.android.linshare.domain.model.autocomplete

import java.util.UUID

data class MailingListAutoCompleteResult(
    override val identifier: String,
    override val display: String,
    val ownerLastName: String,
    val ownerFirstName: String,
    val ownerMail: String,
    val listName: String
) : AutoCompleteResult

fun MailingListAutoCompleteResult.toMailingList(): MailingList {
    return MailingList(
        display = display,
        mailingListId = MailingListId(UUID.fromString(identifier))
    )
}
