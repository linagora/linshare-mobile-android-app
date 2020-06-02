package com.linagora.android.linshare.domain.usecases.autocomplete

import arrow.core.Either
import arrow.core.combineK
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
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
        val autoCompleteFlow = getAutoCompleteSharingInteractor(autoCompletePattern)
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
            autoCompleteCombine == Either.right(Success.Loading) -> transformCombineState(
                contactSuggestionCombine,
                autoCompleteState,
                contactSuggestionState
            )
            contactSuggestionCombine == Either.right(Success.Loading) -> transformCombineState(
                autoCompleteCombine,
                autoCompleteState,
                contactSuggestionState
            )
            autoCompleteState.exists { it is AutoCompleteViewState } && contactSuggestionState.exists { it is ContactSuggestionSuccess } -> {
                transformCombineState(
                    Either.right(CombineReceiverSuggestion),
                    autoCompleteState,
                    contactSuggestionState
                )
            }
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
