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
