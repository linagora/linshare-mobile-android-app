package com.linagora.android.linshare.domain.usecases.autocomplete

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.contact.SimpleContact
import com.linagora.android.linshare.domain.model.contact.toAutoCompleteResult
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_RESULTS
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_STATE
import com.linagora.android.testshared.ShareFixtures.CONTACT_SUGGESTION_RESULTS
import com.linagora.android.testshared.ShareFixtures.CONTACT_SUGGESTION_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetReceiverSuggestionInteractorTest {

    @Mock
    lateinit var getAutoCompleteSharingInteractor: GetAutoCompleteSharingInteractor

    @Mock
    lateinit var getContactSuggestionInteractor: GetContactSuggestionInteractor

    private lateinit var getReceiverSuggestionInteractor: GetReceiverSuggestionInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getReceiverSuggestionInteractor = GetReceiverSuggestionInteractor(
            getContactSuggestionInteractor,
            getAutoCompleteSharingInteractor)
    }

    @Test
    fun getReceiverSuggestionShouldReturnMatchedListCombineFromLinShareAndDevice() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")
        val expectedSuggestion = USER_AUTOCOMPLETE_RESULTS
            .plus(CONTACT_SUGGESTION_RESULTS.map(SimpleContact::toAutoCompleteResult))

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(2000)
                    emit(CONTACT_SUGGESTION_STATE)
                } }

        `when`(getAutoCompleteSharingInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(USER_AUTOCOMPLETE_STATE)
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())
            .last()

        assertThat(lastResult).isEqualTo(Either.right(AutoCompleteViewState(expectedSuggestion)))
    }

    @Test
    fun getReceiverSuggestionShouldReturnPartialMatchedListFromLinShare() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")
        val expectedPartialSuggestion = USER_AUTOCOMPLETE_RESULTS

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(2000)
                    emit(CONTACT_SUGGESTION_STATE)
                } }

        `when`(getAutoCompleteSharingInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(500)
                    emit(USER_AUTOCOMPLETE_STATE)
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())

        assertThat(lastResult).hasSize(3)
        assertThat(lastResult[lastResult.size - 2]).isEqualTo(Either.right(AutoCompleteViewState(expectedPartialSuggestion)))
    }

    @Test
    fun getReceiverSuggestionShouldReturnPartialMatchedListFromDevice() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")
        val expectedPartialSuggestion = CONTACT_SUGGESTION_RESULTS
            .map(SimpleContact::toAutoCompleteResult)

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(200)
                    emit(CONTACT_SUGGESTION_STATE)
                } }

        `when`(getAutoCompleteSharingInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(USER_AUTOCOMPLETE_STATE)
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())

        assertThat(lastResult).hasSize(3)
        assertThat(lastResult[lastResult.size - 2]).isEqualTo(Either.right(AutoCompleteViewState(expectedPartialSuggestion)))
    }

    @Test
    fun getReceiverSuggestionShouldReturnLoadingWhenQueryFromDeviceButNoResultQueryFromLinShare() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(200)
                    emit(CONTACT_SUGGESTION_STATE)
                } }

        `when`(getAutoCompleteSharingInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(Either.left(AutoCompleteNoResult(pattern)))
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())

        assertThat(lastResult).hasSize(3)
        assertThat(lastResult[0]).isEqualTo(LOADING_STATE)
    }

    @Test
    fun getReceiverSuggestionShouldReturnLoadingWhenQueryFromLinShareButNoResultQueryFromDevice() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(Either.left(AutoCompleteNoResult(pattern)))
                } }

        `when`(getAutoCompleteSharingInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(100)
                    emit(USER_AUTOCOMPLETE_STATE)
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())

        assertThat(lastResult).hasSize(3)
        assertThat(lastResult[0]).isEqualTo(LOADING_STATE)
    }

    @Test
    fun getReceiverSuggestionShouldReturnNoResultWhenNoResultQueryFromLinShareAndQueryFromDeviceFail() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(Either.left(ContactSuggestionFailure(RuntimeException())))
                } }

        `when`(getAutoCompleteSharingInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(100)
                    emit(Either.left(AutoCompleteNoResult(pattern)))
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())
            .last()

        assertThat(lastResult).isEqualTo(Either.left(ReceiverSuggestionNoResult(pattern)))
    }
}
