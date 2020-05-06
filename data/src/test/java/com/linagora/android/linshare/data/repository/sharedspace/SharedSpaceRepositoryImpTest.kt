package com.linagora.android.linshare.data.repository.sharedspace

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.testshared.SharedSpaceDocumentFixtures
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceFixtures.QUERY_STRING_SHARED_SPACE
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_1
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SharedSpaceRepositoryImpTest {

    @Mock
    lateinit var sharedSpaceDataSource: SharedSpaceDataSource

    private lateinit var sharedSpaceRepositoryImp: SharedSpaceRepositoryImp

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sharedSpaceRepositoryImp = SharedSpaceRepositoryImp(sharedSpaceDataSource)
    }

    @Test
    fun getAllSharedSpaceShouldReturnSharedSpaceList() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.getSharedSpaces())
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_2) }

            val sharedSpace = sharedSpaceRepositoryImp.getSharedSpaces()
            assertThat(sharedSpace).containsExactly(SHARED_SPACE_1, SHARED_SPACE_2)
        }
    }

    @Test
    fun getAllSharedSpaceShouldReturnEmptyListWhenNoSharedSpaceExist() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.getSharedSpaces())
                .thenAnswer { emptyList<SharedSpaceNodeNested>() }

            val sharedSpace = sharedSpaceRepositoryImp.getSharedSpaces()
            assertThat(sharedSpace).isEmpty()
        }
    }

    @Test
    fun getSharedSpaceShouldReturnAnExistedSharedSpace() = runBlockingTest {
        `when`(
            sharedSpaceDataSource.getSharedSpace(
                SHARED_SPACE_ID_1,
                MembersParameter.WithoutMembers,
                RolesParameter.WithRole
            )
        )
            .thenAnswer { SharedSpaceDocumentFixtures.SHARED_SPACE_1 }

        val sharedSpace = sharedSpaceRepositoryImp
            .getSharedSpace(
                SHARED_SPACE_ID_1,
                MembersParameter.WithoutMembers,
                RolesParameter.WithRole
            )

        assertThat(sharedSpace).isEqualTo(SharedSpaceDocumentFixtures.SHARED_SPACE_1)
    }

    @Test
    fun searchShouldReturnResultList() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.searchSharedSpaces(QUERY_STRING_SHARED_SPACE))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_2) }

            val documents = sharedSpaceRepositoryImp.search(QUERY_STRING_SHARED_SPACE)
            assertThat(documents).containsExactly(SHARED_SPACE_1, SHARED_SPACE_2)
        }
    }

    @Test
    fun searchShouldReturnResultEmptyList() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.searchSharedSpaces(QUERY_STRING_SHARED_SPACE))
                .thenAnswer { emptyList<SharedSpaceNodeNested>() }

            val documents = sharedSpaceRepositoryImp.search(QUERY_STRING_SHARED_SPACE)
            assertThat(documents).isEmpty()
        }
    }
}
