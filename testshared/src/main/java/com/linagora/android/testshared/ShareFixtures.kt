package com.linagora.android.testshared

import arrow.core.Either
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.model.autocomplete.MailingListId
import com.linagora.android.linshare.domain.model.contact.SimpleContact
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareId
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.usecases.autocomplete.ContactSuggestionSuccess
import com.linagora.android.linshare.domain.usecases.receivedshare.ReceivedSharesViewState
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.ShareViewState
import okhttp3.MediaType
import java.util.Date
import java.util.UUID

object ShareFixtures {
    val RECIPIENT_1 = GenericUser(
        mail = "user3@linshare.org",
        firstName = "Joe",
        lastName = "Doe"
    )

    val RECIPIENT_2 = GenericUser(
        mail = "user2@linshare.org",
        firstName = "Jane",
        lastName = "Smith"
    )

    private val MAILING_LIST_ID_1 = MailingListId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"))

    val MAILING_LIST_1 = MailingList(
        display = "Contact 1",
        mailingListId = MAILING_LIST_ID_1
    )

    private val MAILING_LIST_ID_2 = MailingListId(UUID.fromString("6c0e1f35-89e5-6bc3-a8d4-156ec8074beb"))

    val MAILING_LIST_2 = MailingList(
        display = "Contact 2",
        mailingListId = MAILING_LIST_ID_2
    )

    val SHARE_CREATION_1 = ShareRequest(
        mailingListIds = setOf(MAILING_LIST_ID_1),
        recipients = listOf(RECIPIENT_1),
        documentIds = listOf(TestFixtures.Documents.DOCUMENT_ID.uuid)
    )

    val SHARE_CREATION_2 = ShareRequest(
        mailingListIds = setOf(MAILING_LIST_ID_1, MAILING_LIST_ID_2),
        recipients = listOf(RECIPIENT_1, RECIPIENT_2),
        documentIds = listOf(TestFixtures.Documents.DOCUMENT_ID.uuid)
    )

    val SHARE_1 = Share(
        shareId = ShareId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")),
        description = "",
        name = "document.txt",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        expirationDate = Date(1582786676962),
        downloaded = 0,
        document = TestFixtures.Documents.DOCUMENT,
        recipient = RECIPIENT_1,
        type = MediaType.get("text/plain"),
        size = 25,
        message = "",
        hasThumbnail = false,
        ciphered = false,
        sender = TestFixtures.Accounts.LINSHARE_USER
    )

    val SHARE_2 = Share(
        shareId = ShareId(UUID.fromString("6c0e1f35-89e5-6bc3-a8d4-156ec8074beb")),
        description = "",
        name = "document.txt",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        expirationDate = Date(1582786676962),
        downloaded = 0,
        document = TestFixtures.Documents.DOCUMENT,
        recipient = RECIPIENT_2,
        type = MediaType.get("text/plain"),
        size = 25,
        message = "",
        hasThumbnail = false,
        ciphered = false,
        sender = TestFixtures.Accounts.LINSHARE_USER
    )

    val SHARE_STATE_WITH_ONE_SHARE = Either.right(ShareViewState(listOf(SHARE_1)))

    val SHARE_STATE_WITH_MULTIPLE_SHARES = Either.right(ShareViewState(listOf(SHARE_1, SHARE_2)))

    private val RECEIVED_LIST_VIEW_STATE = ReceivedSharesViewState(listOf(SHARE_1, SHARE_2))

    val ALL_RECEIVED_STATE = Either.Right(RECEIVED_LIST_VIEW_STATE)

    val ADD_RECIPIENT_1_STATE = Either.right(AddRecipient(RECIPIENT_1))

    val ADD_RECIPIENT_2_STATE = Either.right(AddRecipient(RECIPIENT_2))

    val CONTACT_1 = SimpleContact("Barbra Adkin", "badkin@hotmail.com")

    val CONTACT_2 = SimpleContact("Barrett Toyama", "barrett.toyama@toyama.org")

    val CONTACT_3 = SimpleContact("Glen Bartolet", "glen_bartolet@hotmail.com")

    val CONTACT_4 = SimpleContact("Stephaine Barfield", "stephaine@barfield.com")

    val CONTACT_SUGGESTION_RESULTS = listOf(CONTACT_1, CONTACT_2, CONTACT_3, CONTACT_4)

    val CONTACT_SUGGESTION_STATE = Either.Right(ContactSuggestionSuccess(CONTACT_SUGGESTION_RESULTS))
}
