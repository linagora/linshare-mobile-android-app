package com.linagora.android.linshare.data.repository.sharedspace

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.sharedspace.roles.SharedSpaceRoleDataSource
import com.linagora.android.testshared.SharedSpaceFixtures.ADMIN_ROLE
import com.linagora.android.testshared.SharedSpaceFixtures.CONTRIBUTOR_ROLE
import com.linagora.android.testshared.SharedSpaceFixtures.READER_ROLE
import com.linagora.android.testshared.SharedSpaceFixtures.WRITER_ROLE
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SharedSpaceRoleRepositoryImpTest {
    @Mock
    lateinit var sharedSpaceRoleDataSource: SharedSpaceRoleDataSource

    private lateinit var sharedSpaceRoleRepositoryImp: SharedSpaceRoleRepositoryImp

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sharedSpaceRoleRepositoryImp = SharedSpaceRoleRepositoryImp(sharedSpaceRoleDataSource)
    }

    @Test
    fun findAllShouldReturnAllRoles() = runBlockingTest {
        `when`(sharedSpaceRoleDataSource.findAll())
            .thenAnswer { listOf(ADMIN_ROLE, CONTRIBUTOR_ROLE, WRITER_ROLE, READER_ROLE) }

        val roles = sharedSpaceRoleRepositoryImp.findAll()
        assertThat(roles).containsExactly(
            ADMIN_ROLE, CONTRIBUTOR_ROLE, WRITER_ROLE, READER_ROLE
        )
    }
}
