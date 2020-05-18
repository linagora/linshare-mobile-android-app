package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.PARENT_NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.QUERY_SHARED_SPACE_DOCUMENT
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SEARCH_SHARED_SPACE_DOCUMENT_STATE
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SearchSharedSpaceDocumentInteractorTest {
    @Mock
    lateinit var sharedSpacesDocumentRepository: SharedSpacesDocumentRepository

    private lateinit var searchSharedSpaceDocumentInteractor: SearchSharedSpaceDocumentInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchSharedSpaceDocumentInteractor = SearchSharedSpaceDocumentInteractor(sharedSpacesDocumentRepository)
    }

    @Test
    fun searchShouldReturnSuccessStateWithMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val states = searchSharedSpaceDocumentInteractor(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(SEARCH_SHARED_SPACE_DOCUMENT_STATE)
    }

    @Test
    fun searchShouldReturnEmptyStateWhenNoResultMatched() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { emptyList<WorkGroupNode>() }

        val states = searchSharedSpaceDocumentInteractor(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(Either.Left(SearchSharedSpaceDocumentNoResult))
    }

    @Test
    fun searchShouldReturnFailureStateWhenSearchHaveError() = runBlockingTest {
        val exception = RuntimeException("Search document failed")
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenThrow(exception)

        val states = searchSharedSpaceDocumentInteractor(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(Either.left(SharedSpaceDocumentFailure(exception)))
    }
}
