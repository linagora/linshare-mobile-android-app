package com.linagora.android.testshared

import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import java.util.UUID

object AutoCompleteFixtures {

    val USER_AUTOCOMPLETE_1 = UserAutoCompleteResult(
        identifier = UUID.fromString("b8dd1237-bca1-4005-943a-000b4afb3820"),
        display = "user5@linshare.org",
        firstName = "Parker",
        lastName = "Peter",
        domain = UUID.fromString("118629de-3bc6-47cf-9086-156ec8074beb"),
        mail = "user5@linshare.org"
    )

    val USER_AUTOCOMPLETE_2 = UserAutoCompleteResult(
        identifier = UUID.fromString("ff00ee96-3cd3-4d60-88a4-097da028c2df"),
        display = "user6@linshare.org",
        firstName = "Wayne",
        lastName = "Bruce",
        domain = UUID.fromString("118629de-3bc6-47cf-9086-156ec8074beb"),
        mail = "user6@linshare.org"
    )
}
