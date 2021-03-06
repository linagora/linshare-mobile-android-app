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

package com.linagora.android.linshare.domain.usecases.autocomplete

import arrow.core.Either
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.autocomplete.ThreadMemberAutoCompleteResult
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.repository.autocomplete.AutoCompleteRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAutoCompleteSharingInteractor @Inject constructor(
    private val autoCompleteRepository: AutoCompleteRepository
) {

    operator fun invoke(
        autoCompletePattern: AutoCompletePattern,
        autoCompleteType: AutoCompleteType,
        threadId: SharedSpaceId? = null
    ): Flow<Either<Failure, Success>> {
        return flow<Either<Failure, Success>> {
            emit(Either.right(Success.Loading))

            val autoCompleteState = Either
                .catch { autoCompleteRepository.getAutoComplete(autoCompletePattern, autoCompleteType, threadId) }
                .fold(
                    ifLeft = { Either.left(AutoCompleteFailure(it)) },
                    ifRight = { getAutoCompleteState(autoCompletePattern, autoCompleteType, it) })

            emit(autoCompleteState)
        }
    }

    private fun getAutoCompleteState(
        pattern: AutoCompletePattern,
        autoCompleteType: AutoCompleteType,
        userAutoCompleteResults: List<AutoCompleteResult>
    ): Either<Failure, Success> {
        return userAutoCompleteResults
            .takeIf { it.isNotEmpty() }
            ?.let { generateStateBaseOnAutoCompleteType(autoCompleteType, userAutoCompleteResults) }
            ?.let { Either.right(it) }
            ?: Either.left(AutoCompleteNoResult(pattern))
    }

    private fun generateStateBaseOnAutoCompleteType(
        autoCompleteType: AutoCompleteType,
        userAutoCompleteResults: List<AutoCompleteResult>
    ): Success.ViewState {
        return when (autoCompleteType) {
            AutoCompleteType.SHARING -> AutoCompleteViewState(userAutoCompleteResults)
            AutoCompleteType.THREAD_MEMBERS -> ThreadMembersAutoCompleteViewState(
                userAutoCompleteResults.filterNot(this::isMember))
        }
    }

    private fun isMember(autoCompleteResult: AutoCompleteResult): Boolean {
        if (autoCompleteResult is ThreadMemberAutoCompleteResult) {
            return autoCompleteResult.isMember
        }
        return false
    }
}
