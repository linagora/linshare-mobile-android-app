package com.linagora.android.linshare.domain.usecases.autocomplete

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.repository.autocomplete.AutoCompleteRepository
import com.linagora.android.testshared.AutoCompleteFixtures.NO_RESULT_USER_AUTOCOMPLETE_STATE
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_1
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_2
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetAutoCompleteSharingInteractorTest {

    @Mock
    lateinit var autoCompleteRepository: AutoCompleteRepository

    private lateinit var autoCompleteSharingInteractor: GetAutoCompleteSharingInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        autoCompleteSharingInteractor = GetAutoCompleteSharingInteractor(autoCompleteRepository)
    }

    @Test
    fun getAutoCompleteSharingShouldReturnResultWhenPatternMatched() = runBlockingTest {
        `when`(autoCompleteRepository.getAutoComplete(AutoCompletePattern("user"), AutoCompleteType.SHARING))
            .thenAnswer { listOf(USER_AUTOCOMPLETE_1, USER_AUTOCOMPLETE_2) }

        assertThat(autoCompleteSharingInteractor(AutoCompletePattern("user"))
                .map { it }
                .toList(ArrayList()))
            .containsExactly(LOADING_STATE, USER_AUTOCOMPLETE_STATE)
    }

    @Test
    fun getAutoCompleteSharingShouldReturnEmptyWhenPatternUnMatched() = runBlockingTest {
        `when`(autoCompleteRepository.getAutoComplete(AutoCompletePattern("invalid"), AutoCompleteType.SHARING))
            .thenAnswer { emptyList<UserAutoCompleteResult>() }

        assertThat(autoCompleteSharingInteractor(AutoCompletePattern("invalid"))
                .map { it }
                .toList(ArrayList()))
            .containsExactly(LOADING_STATE, NO_RESULT_USER_AUTOCOMPLETE_STATE)
    }
}
