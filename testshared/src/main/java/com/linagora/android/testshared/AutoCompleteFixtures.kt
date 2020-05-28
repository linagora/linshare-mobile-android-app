package com.linagora.android.testshared

import arrow.core.Either
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteNoResult
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteViewState
import java.util.UUID

object AutoCompleteFixtures {

    val USER_AUTOCOMPLETE_1 = UserAutoCompleteResult(
        identifier = "b8dd1237-bca1-4005-943a-000b4afb3820",
        display = "user5@linshare.org",
        firstName = "Parker",
        lastName = "Peter",
        domain = UUID.fromString("118629de-3bc6-47cf-9086-156ec8074beb"),
        mail = "user5@linshare.org"
    )

    val USER_AUTOCOMPLETE_2 = UserAutoCompleteResult(
        identifier = "ff00ee96-3cd3-4d60-88a4-097da028c2df",
        display = "user6@linshare.org",
        firstName = "Wayne",
        lastName = "Bruce",
        domain = UUID.fromString("118629de-3bc6-47cf-9086-156ec8074beb"),
        mail = "user6@linshare.org"
    )

    val USER_AUTOCOMPLETE_STATE = Either.right(
        AutoCompleteViewState(listOf(USER_AUTOCOMPLETE_1, USER_AUTOCOMPLETE_2)))

    val NO_RESULT_USER_AUTOCOMPLETE_STATE = Either.right(AutoCompleteNoResult(AutoCompletePattern("invalid")))
}
