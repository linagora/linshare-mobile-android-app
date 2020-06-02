package com.linagora.android.linshare.domain.usecases.autocomplete

import arrow.core.Either
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.contact.Contact
import com.linagora.android.linshare.domain.repository.contact.ContactRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetContactSuggestionInteractor @Inject constructor(
    private val contactRepository: ContactRepository
) {
    operator fun invoke(autoCompletePattern: AutoCompletePattern): Flow<Either<Failure, Success>> {
        return flow<Either<Failure, Success>> {
            emit(Either.right(Success.Loading))

            val contactSuggestionState = Either
                .catch { contactRepository.getContactsSuggestion(autoCompletePattern) }
                .fold(
                    ifLeft = { Either.left(ContactSuggestionFailure(it)) },
                    ifRight = { generateContactSuggestionState(it, autoCompletePattern) })

            emit(contactSuggestionState)
        }
    }

    private fun generateContactSuggestionState(
        contactsResult: List<Contact>,
        autoCompletePattern: AutoCompletePattern
    ): Either<Failure, Success> {
        return contactsResult
            .takeIf { it.isNotEmpty() }
            ?.let(::ContactSuggestionSuccess)
            ?.let { Either.right(it) }
            ?: Either.left(AutoCompleteNoResult(autoCompletePattern))
    }
}
