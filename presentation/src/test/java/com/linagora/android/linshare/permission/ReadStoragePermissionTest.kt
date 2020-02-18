package com.linagora.android.linshare.permission

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowReadStorage
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowReadStorage
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ReadStoragePermissionTest {

    @Mock
    lateinit var propertiesRepository: PropertiesRepository

    private lateinit var readStoragePermission: ReadStoragePermission

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        readStoragePermission = ReadStoragePermission(propertiesRepository)
    }

    @Test
    fun shouldShowReadStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldShow() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForReadStoragePermission())
                .thenAnswer { PreviousUserPermissionAction.NONE }

            assertThat(readStoragePermission.shouldShowPermissionRequest(ShouldShowReadStorage))
                .isEqualTo(ShouldShowReadStorage)
        }
    }

    @Test
    fun shouldShowReadStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldBeNotShow() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForReadStoragePermission())
                .thenAnswer { PreviousUserPermissionAction.NONE }

            assertThat(readStoragePermission.shouldShowPermissionRequest(ShouldNotShowReadStorage))
                .isEqualTo(ShouldShowReadStorage)
        }
    }

    @Test
    fun shouldShowReadStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldShowAndUserDenied() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForReadStoragePermission())
                .thenAnswer { PreviousUserPermissionAction.DENIED }

            assertThat(readStoragePermission.shouldShowPermissionRequest(ShouldShowReadStorage))
                .isEqualTo(ShouldShowReadStorage)
        }
    }

    @Test
    fun shouldShowReadStoragePermissionRequestShouldReturnShouldNotShowWhenSystemShouldNotShowAndUserDenied() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForReadStoragePermission())
                .thenAnswer { PreviousUserPermissionAction.DENIED }

            assertThat(readStoragePermission.shouldShowPermissionRequest(ShouldNotShowReadStorage))
                .isEqualTo(ShouldNotShowReadStorage)
        }
    }
}
