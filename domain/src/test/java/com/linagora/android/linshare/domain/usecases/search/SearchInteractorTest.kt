package com.linagora.android.linshare.domain.usecases.search

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_2
import com.linagora.android.testshared.TestFixtures.Searchs.NOT_FOUND_STATE
import com.linagora.android.testshared.TestFixtures.Searchs.QUERY_STRING
import com.linagora.android.testshared.TestFixtures.Searchs.SEARCH_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SearchInteractorTest {

    @Mock
    lateinit var documentRepository: DocumentRepository

    private lateinit var searchInteractor: SearchInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchInteractor = SearchInteractor(documentRepository)
    }

    @Test
    fun searchShouldSuccessReturnSearchResults() {
        runBlockingTest {
            `when`(documentRepository.search(QUERY_STRING))
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2) }

            val states = searchInteractor(QUERY_STRING)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(SEARCH_SUCCESS_STATE)
        }
    }

    @Test
    fun searchShouldReturnSearchInitialWhileQueryLengthIsLowerThanThree() {
        runBlockingTest {
            val states = searchInteractor("te")
                .toList(ArrayList())

            assertThat(states).hasSize(1)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(Either.right(SearchInitial))
        }
    }

    @Test
    fun searchShouldReturnNotFound() {
        runBlockingTest {
            `when`(documentRepository.search(QUERY_STRING))
                .thenAnswer { emptyList<Document>() }

            val states = searchInteractor(QUERY_STRING)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(NOT_FOUND_STATE)
        }
    }
}
