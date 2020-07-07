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
import arrow.core.combineK
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.contact.Contact
import com.linagora.android.linshare.domain.model.contact.toAutoCompleteResult
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetReceiverSuggestionInteractor @Inject constructor(
    private val getContactSuggestionInteractor: GetContactSuggestionInteractor,
    private val getAutoCompleteSharingInteractor: GetAutoCompleteSharingInteractor
) {

    operator fun invoke(autoCompletePattern: AutoCompletePattern): Flow<Either<Failure, Success>> {
        val autoCompleteFlow = getAutoCompleteSharingInteractor(autoCompletePattern, AutoCompleteType.SHARING)
        val contactSuggestionFlow = getContactSuggestionInteractor(autoCompletePattern)
        return autoCompleteFlow.combine(contactSuggestionFlow) { autoCompleteState, contactSuggestionState ->
            combineReceiverSuggestionState(autoCompletePattern, autoCompleteState, contactSuggestionState) }
    }

    private fun combineReceiverSuggestionState(
        autoCompletePattern: AutoCompletePattern,
        autoCompleteState: Either<Failure, Success>,
        contactSuggestionState: Either<Failure, Success>
    ): Either<Failure, Success> {
        val autoCompleteCombine = autoCompleteState.combineK(contactSuggestionState)
        val contactSuggestionCombine = contactSuggestionState.combineK(autoCompleteState)

        return when {
            autoCompleteCombine == Either.right(Success.Loading) ->
                transformCombineState(
                    contactSuggestionCombine,
                    autoCompleteState,
                    contactSuggestionState
                )
            contactSuggestionCombine == Either.right(Success.Loading) ->
                transformCombineState(
                    autoCompleteCombine,
                    autoCompleteState,
                    contactSuggestionState
                )
            autoCompleteState.exists { it is AutoCompleteViewState } || contactSuggestionState.exists { it is ContactSuggestionSuccess } ->
                transformCombineState(
                    Either.right(CombineReceiverSuggestion),
                    autoCompleteState,
                    contactSuggestionState
                )
            else -> Either.left(ReceiverSuggestionNoResult(autoCompletePattern))
        }
    }

    private fun transformCombineState(
        combineState: Either<Failure, Success>,
        autoCompleteState: Either<Failure, Success>,
        contactSuggestionState: Either<Failure, Success>
    ): Either<Failure, Success> = combineState.map { success ->
        when (success) {
            is AutoCompleteViewState -> AutoCompleteViewState(
                combineReceiverSuggestionItem(autoCompleteState, contactSuggestionState)
            )
            is ContactSuggestionSuccess -> AutoCompleteViewState(
                combineReceiverSuggestionItem(autoCompleteState, contactSuggestionState)
            )
            is CombineReceiverSuggestion -> AutoCompleteViewState(
                combineReceiverSuggestionItem(autoCompleteState, contactSuggestionState)
            )
            else -> success
        }
    }

    private fun combineReceiverSuggestionItem(
        autoCompleteState: Either<Failure, Success>,
        contactSuggestionState: Either<Failure, Success>
    ): List<AutoCompleteResult> {
        val autoCompleteResult = autoCompleteState.fold(
            ifLeft = { emptyList<AutoCompleteResult>() },
            ifRight = { success ->
                success.takeIf { it is AutoCompleteViewState }
                    ?.let { (it as AutoCompleteViewState).results }
                    ?: emptyList()
            }
        )

        val contactSuggestionResult = contactSuggestionState.fold(
            ifLeft = { emptyList<AutoCompleteResult>() },
            ifRight = { success ->
                success.takeIf { it is ContactSuggestionSuccess }
                    ?.let { (it as ContactSuggestionSuccess).suggestions }
                    ?.map(Contact::toAutoCompleteResult)
                    ?: emptyList()
            }
        )

        return autoCompleteResult.plus(contactSuggestionResult)
    }
}
