/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

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
