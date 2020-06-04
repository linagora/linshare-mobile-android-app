package com.linagora.android.linshare.permission

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowReadContact
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowReadContact
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ReadContactPermissionTest {

    @Mock
    lateinit var propertiesRepository: PropertiesRepository

    private lateinit var readContactPermission: ReadContactPermission

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        readContactPermission = ReadContactPermission(propertiesRepository)
    }

    @Test
    fun shouldShowReadStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldShow() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForReadContactPermission())
                .thenAnswer { PreviousUserPermissionAction.NONE }

            assertThat(readContactPermission.shouldShowPermissionRequest(ShouldShowReadContact))
                .isEqualTo(ShouldShowReadContact)
        }
    }

    @Test
    fun shouldShowReadStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldBeNotShow() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForReadContactPermission())
                .thenAnswer { PreviousUserPermissionAction.NONE }

            assertThat(readContactPermission.shouldShowPermissionRequest(ShouldNotShowReadContact))
                .isEqualTo(ShouldShowReadContact)
        }
    }

    @Test
    fun shouldShowReadStoragePermissionRequestShouldReturnShouldShowWhenSystemShouldShowAndUserDenied() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForReadContactPermission())
                .thenAnswer { PreviousUserPermissionAction.DENIED }

            assertThat(readContactPermission.shouldShowPermissionRequest(ShouldShowReadContact))
                .isEqualTo(ShouldShowReadContact)
        }
    }

    @Test
    fun shouldShowReadStoragePermissionRequestShouldReturnShouldNotShowWhenSystemShouldNotShowAndUserDenied() {
        runBlockingTest {
            Mockito.`when`(propertiesRepository.getRecentActionForReadContactPermission())
                .thenAnswer { PreviousUserPermissionAction.DENIED }

            assertThat(readContactPermission.shouldShowPermissionRequest(ShouldNotShowReadContact))
                .isEqualTo(ShouldNotShowReadContact)
        }
    }
}
