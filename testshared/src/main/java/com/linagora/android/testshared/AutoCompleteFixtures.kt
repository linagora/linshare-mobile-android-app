package com.linagora.android.testshared

import arrow.core.Either
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.ThreadMemberAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteNoResult
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteViewState
import com.linagora.android.linshare.domain.usecases.autocomplete.ThreadMembersAutoCompleteViewState
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

    val USER_AUTOCOMPLETE_RESULTS = listOf(USER_AUTOCOMPLETE_1, USER_AUTOCOMPLETE_2)

    val USER_AUTOCOMPLETE_STATE = Either.right(
        AutoCompleteViewState(USER_AUTOCOMPLETE_RESULTS))

    val NO_RESULT_USER_AUTOCOMPLETE_STATE = Either.left(AutoCompleteNoResult(AutoCompletePattern("invalid")))

    val THREAD_MEMBER_AUTO_COMPLETE_RESULT_1 = ThreadMemberAutoCompleteResult(
        identifier = "ff00ee96-3cd3-4d60-88a4-097da028c2df",
        display = "user6@linshare.org",
        firstName = "Wayne",
        lastName = "Bruce",
        domain = UUID.fromString("118629de-3bc6-47cf-9086-156ec8074beb"),
        mail = "user6@linshare.org",
        userUuid = UUID.fromString("ff00ee96-3cd3-4d60-88a4-097da028c2df"),
        threadUuid = UUID.fromString("49ac407c-df87-49b1-b961-90d7eafe4217"),
        isMember = false
    )

    val THREAD_MEMBER_AUTO_COMPLETE_RESULT_2 = ThreadMemberAutoCompleteResult(
        identifier = "b8dd1237-bca1-4005-943a-000b4afb3820",
        display = "user5@linshare.org",
        firstName = "Parker",
        lastName = "Peter",
        domain = UUID.fromString("118629de-3bc6-47cf-9086-156ec8074beb"),
        mail = "user5@linshare.org",
        userUuid = UUID.fromString("b8dd1237-bca1-4005-943a-000b4afb3820"),
        threadUuid = UUID.fromString("49ac407c-df87-49b1-b961-90d7eafe4217"),
        isMember = false
    )

    val THREAD_MEMBER_AUTO_COMPLETE_STATE = Either.right(
        ThreadMembersAutoCompleteViewState(listOf(THREAD_MEMBER_AUTO_COMPLETE_RESULT_1, THREAD_MEMBER_AUTO_COMPLETE_RESULT_2)))
}
