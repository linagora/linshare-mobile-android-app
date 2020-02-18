package com.linagora.android.linshare.permission

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import com.linagora.android.linshare.model.permission.PermissionName
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowWriteStorage
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowWriteStorage
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WriteStoragePermission @Inject constructor(
    private val propertiesRepository: PropertiesRepository
) : Permission {
    override fun permissionName() = PermissionName(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun requestCode() = WriteExternalPermissionRequestCode

    override fun systemShouldShowPermissionRequest(activity: Activity): RuntimePermissionRequest {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName().name)) {
            return ShouldShowWriteStorage
        }
        return ShouldNotShowWriteStorage
    }

    override suspend fun setActionForPermissionRequest(previousUserPermissionAction: PreviousUserPermissionAction) {
        propertiesRepository.storeRecentActionForWriteStoragePermission(previousUserPermissionAction)
    }

    override suspend fun getActionForPermissionRequest(): PreviousUserPermissionAction {
        return propertiesRepository.getRecentActionForWriteStoragePermission()
    }

    override suspend fun shouldShowPermissionRequest(systemShouldShow: RuntimePermissionRequest): RuntimePermissionRequest {
        val userPermissionAction = propertiesRepository.getRecentActionForWriteStoragePermission()
        return combineWriteStoragePermission(userPermissionAction, systemShouldShow)
    }

    private fun combineWriteStoragePermission(
        previousUserPermissionAction: PreviousUserPermissionAction,
        systemRuntimePermissionRequest: RuntimePermissionRequest
    ): RuntimePermissionRequest {
        if (previousUserPermissionAction != DENIED) {
            return ShouldShowWriteStorage
        }

        if (systemRuntimePermissionRequest == ShouldShowWriteStorage) {
            return ShouldShowWriteStorage
        }

        return ShouldNotShowWriteStorage
    }
}
