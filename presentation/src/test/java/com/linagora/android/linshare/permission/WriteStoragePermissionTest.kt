package com.linagora.android.linshare.permission

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.NONE
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowWriteStorage
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowWriteStorage
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class WriteStoragePermissionTest {

    @Mock
    lateinit var propertiesRepository: PropertiesRepository

    private lateinit var writeStoragePermission: WriteStoragePermission

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        writeStoragePermission = WriteStoragePermission(propertiesRepository)
    }

    @Test
    fun shouldShowWriteStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldShow() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForWriteStoragePermission())
                .thenAnswer { NONE }

            assertThat(writeStoragePermission.shouldShowPermissionRequest(ShouldShowWriteStorage))
                .isEqualTo(ShouldShowWriteStorage)
        }
    }

    @Test
    fun shouldShowWriteStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldBeNotShow() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForWriteStoragePermission())
                .thenAnswer { NONE }

            assertThat(writeStoragePermission.shouldShowPermissionRequest(ShouldNotShowWriteStorage))
                .isEqualTo(ShouldShowWriteStorage)
        }
    }

    @Test
    fun shouldShowWriteStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldShowAndUserDenied() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForWriteStoragePermission())
                .thenAnswer { DENIED }

            assertThat(writeStoragePermission.shouldShowPermissionRequest(ShouldShowWriteStorage))
                .isEqualTo(ShouldShowWriteStorage)
        }
    }

    @Test
    fun shouldShowWriteStoragePermissionRequestShouldReturnShouldNotShowWhenSystemShouldNotShowAndUserDenied() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForWriteStoragePermission())
                .thenAnswer { DENIED }

            assertThat(writeStoragePermission.shouldShowPermissionRequest(ShouldNotShowWriteStorage))
                .isEqualTo(ShouldNotShowWriteStorage)
        }
    }
}
