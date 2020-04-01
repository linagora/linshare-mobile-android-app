package com.linagora.android.linshare.data.repository.autocomplete

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.autocomplete.AutoCompleteDataSource
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_1
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class LinshareAutoCompleteRepositoryTest {

    @Mock
    lateinit var autoCompleteDataSource: AutoCompleteDataSource

    private lateinit var linshareAutoCompleteRepository: LinshareAutoCompleteRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        linshareAutoCompleteRepository = LinshareAutoCompleteRepository(autoCompleteDataSource)
    }

    @Test
    fun getAutoCompleteWithSharingTypeShouldReturnUserAutoCompleteResult() = runBlockingTest {
        `when`(autoCompleteDataSource.getAutoComplete(
            autoCompletePattern = AutoCompletePattern("user"),
            autoCompleteType = AutoCompleteType.SHARING
        )).thenAnswer { listOf(USER_AUTOCOMPLETE_1, USER_AUTOCOMPLETE_2) }

        val userAutoCompleteResult = linshareAutoCompleteRepository.getAutoComplete(AutoCompletePattern("user"), AutoCompleteType.SHARING)
        assertThat(userAutoCompleteResult).hasSize(2)
        assertThat(userAutoCompleteResult).containsExactly(USER_AUTOCOMPLETE_1, USER_AUTOCOMPLETE_2)
    }

    @Test
    fun getAutoCompleteWithSharingTypeShouldReturnEmptyListWhileNoMatchingPattern() = runBlockingTest {
        `when`(autoCompleteDataSource.getAutoComplete(
            autoCompletePattern = AutoCompletePattern("valid"),
            autoCompleteType = AutoCompleteType.SHARING
        )).thenAnswer { emptyList<UserAutoCompleteResult>() }

        val userAutoCompleteResult = linshareAutoCompleteRepository.getAutoComplete(AutoCompletePattern("valid"), AutoCompleteType.SHARING)
        assertThat(userAutoCompleteResult).isEmpty()
    }
}
