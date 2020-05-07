package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.testshared.SharedSpaceFixtures.NOT_FOUND_SHARED_SPACE_STATE
import com.linagora.android.testshared.SharedSpaceFixtures.QUERY_STRING_SHARED_SPACE
import com.linagora.android.testshared.SharedSpaceFixtures.SEARCH_SHARED_SPACE_LIST_VIEW_STATE
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_1
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_2
import com.linagora.android.testshared.TestFixtures.Searchs.QUERY_STRING
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SearchSharedSpaceInteractorTest {

    @Mock
    lateinit var sharedSpaceRepository: SharedSpaceRepository

    private lateinit var searchSharedSpaceInteractor: SearchSharedSpaceInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchSharedSpaceInteractor = SearchSharedSpaceInteractor(sharedSpaceRepository)
    }

    @Test
    fun searchShouldSuccessReturnSearchResults() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_2) }

            val states = searchSharedSpaceInteractor(QUERY_STRING)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(SEARCH_SHARED_SPACE_LIST_VIEW_STATE)
        }
    }

    @Test
    fun searchShouldReturnSearchInitialWhileQueryLengthIsLowerThanThree() {
        runBlockingTest {
            val states = searchSharedSpaceInteractor(QueryString("qu"))
                .toList(ArrayList())

            assertThat(states).hasSize(1)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(Either.right(SearchSharedSpaceInitial))
        }
    }

    @Test
    fun searchShouldReturnNotFound() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { emptyList<Document>() }

            val states = searchSharedSpaceInteractor(QUERY_STRING_SHARED_SPACE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(NOT_FOUND_SHARED_SPACE_STATE)
        }
    }
}
