package com.linagora.android.linshare.domain.usecases.autocomplete

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.contact.Contact
import com.linagora.android.linshare.domain.repository.contact.ContactRepository
import com.linagora.android.testshared.ShareFixtures.CONTACT_1
import com.linagora.android.testshared.ShareFixtures.CONTACT_2
import com.linagora.android.testshared.ShareFixtures.CONTACT_3
import com.linagora.android.testshared.ShareFixtures.CONTACT_4
import com.linagora.android.testshared.ShareFixtures.CONTACT_SUGGESTION_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetContactSuggestionInteractorTest {
    @Mock
    lateinit var contactRepository: ContactRepository

    private lateinit var getContactSuggestionInteractor: GetContactSuggestionInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getContactSuggestionInteractor = GetContactSuggestionInteractor(contactRepository)
    }

    @Test
    fun getContactSuggestionShouldReturnResultWhenPatternMatched() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")
        `when`(contactRepository.getContactsSuggestion(pattern))
            .thenAnswer { listOf(CONTACT_1, CONTACT_2, CONTACT_3, CONTACT_4) }

        assertThat(getContactSuggestionInteractor(pattern)
                .toList(ArrayList()))
            .containsExactly(LOADING_STATE, CONTACT_SUGGESTION_STATE)
    }

    @Test
    fun getContactSuggestionShouldReturnNoResultStateWhenUnMatchedPattern() = runBlockingTest {
        val pattern = AutoCompletePattern("foo")
        `when`(contactRepository.getContactsSuggestion(pattern))
            .thenAnswer { emptyList<Contact>() }

        assertThat(getContactSuggestionInteractor(pattern)
                .toList(ArrayList()))
            .containsExactly(LOADING_STATE, Either.Left(AutoCompleteNoResult(pattern)))
    }
}
