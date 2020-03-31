package com.linagora.android.testshared

import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareCreation
import java.util.Date
import java.util.UUID

object ShareFixtures {
    private val RECIPIENT_1 = GenericUser(
        mail = "user3@linshare.org"
    )

    private val RECIPIENT_2 = GenericUser(
        mail = "user2@linshare.org"
    )

    val SHARE_CREATION_1 = ShareCreation(
        recipients = listOf(RECIPIENT_1),
        documents = listOf(TestFixtures.Documents.DOCUMENT_ID)
    )

    val SHARE_CREATION_2 = ShareCreation(
        recipients = listOf(RECIPIENT_1, RECIPIENT_2),
        documents = listOf(TestFixtures.Documents.DOCUMENT_ID)
    )

    val SHARE_1 = Share(
        uuid = UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"),
        description = "",
        name = "document.txt",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        expirationDate = Date(1582786676962),
        downloaded = 0,
        document = TestFixtures.Documents.DOCUMENT,
        recipient = RECIPIENT_1
    )

    val SHARE_2 = Share(
        uuid = UUID.fromString("6c0e1f35-89e5-6bc3-a8d4-156ec8074beb"),
        description = "",
        name = "document.txt",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        expirationDate = Date(1582786676962),
        downloaded = 0,
        document = TestFixtures.Documents.DOCUMENT,
        recipient = RECIPIENT_2
    )
}
