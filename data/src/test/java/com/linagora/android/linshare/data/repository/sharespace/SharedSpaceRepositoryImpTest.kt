package com.linagora.android.linshare.data.repository.sharespace

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested
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
                .thenAnswer { emptyList<ShareSpaceNodeNested>() }

            val sharedSpace = sharedSpaceRepositoryImp.getSharedSpaces()
            assertThat(sharedSpace).isEmpty()
        }
    }
}
