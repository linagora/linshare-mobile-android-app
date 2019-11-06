package com.linagora.android.linshare.data.repository

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.LinshareDataSource
import com.linagora.android.linshare.data.repository.user.LinshareAuditUserRepository
import com.linagora.android.testshared.TestFixtures.Accounts.LAST_LOGIN
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class LinshareAuditUserRepositoryTest {

    @Mock
    lateinit var linshareDataSource: LinshareDataSource

    private lateinit var linshareAuditUserRepository: LinshareAuditUserRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        linshareAuditUserRepository = LinshareAuditUserRepository(linshareDataSource)
    }

    @Test
    fun getLastLoginShouldSuccess() {
        runBlockingTest {
            `when`(linshareDataSource.getLastLogin())
                .thenAnswer { LAST_LOGIN }

            assertThat(linshareAuditUserRepository.getLastLogin())
                .isEqualTo(LAST_LOGIN)
        }
    }

    @Test
    fun getLastLoginShouldFailedWhenDataSourceFailed() {
        runBlockingTest {
            `when`(linshareDataSource.getLastLogin())
                .thenAnswer { null }

            assertThat(linshareAuditUserRepository.getLastLogin())
                .isNull()
        }
    }
}
