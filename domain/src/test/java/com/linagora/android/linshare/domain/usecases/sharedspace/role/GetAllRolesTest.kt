package com.linagora.android.linshare.domain.usecases.sharedspace.role

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.sharedspace.sharedspaceroles.SharedSpaceRoleRepository
import com.linagora.android.testshared.SharedSpaceFixtures.ADMIN_ROLE
import com.linagora.android.testshared.SharedSpaceFixtures.CONTRIBUTOR_ROLE
import com.linagora.android.testshared.SharedSpaceFixtures.READER_ROLE
import com.linagora.android.testshared.SharedSpaceFixtures.WRITER_ROLE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetAllRolesTest {
    @Mock
    lateinit var sharedSpaceRoleRepository: SharedSpaceRoleRepository

    private lateinit var getAllRoles: GetAllRoles

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getAllRoles = GetAllRoles(sharedSpaceRoleRepository)
    }

    @Test
    fun getAllRolesShouldReturnAllRolesState() = runBlockingTest {
        val roles = listOf(ADMIN_ROLE, CONTRIBUTOR_ROLE, WRITER_ROLE, READER_ROLE)
        `when`(sharedSpaceRoleRepository.findAll())
            .thenAnswer { roles }

        val states = getAllRoles()
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states).containsExactly(
            LOADING_STATE, Either.right(GetAllSharedSpaceRolesSuccess(roles)))
    }

    @Test
    fun getAllRolesShouldReturnFailedStateWhenGetRolesFailedInRepository() = runBlockingTest {
        val exception = RuntimeException("Get roles failed")
        `when`(sharedSpaceRoleRepository.findAll())
            .thenThrow(exception)

        val states = getAllRoles()
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states).containsExactly(LOADING_STATE, Either.left(GetAllSharedSpaceRolesFailed(exception)))
    }
}
