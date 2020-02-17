package com.linagora.android.linshare.permission

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import com.linagora.android.linshare.domain.model.properties.RecentUserPermissionAction
import com.linagora.android.linshare.domain.model.properties.RecentUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import com.linagora.android.linshare.model.permission.PermissionName
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowReadStorage
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowReadStorage
import com.linagora.android.linshare.view.ReadExternalPermissionRequestCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadStoragePermission @Inject constructor(
    private val propertiesRepository: PropertiesRepository
) : Permission {

    override fun requestCode() = ReadExternalPermissionRequestCode

    override fun permissionName() = PermissionName(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun systemShouldShowPermissionRequest(activity: Activity): RuntimePermissionRequest {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName().name)) {
            return ShouldShowReadStorage
        }
        return ShouldNotShowReadStorage
    }

    override suspend fun setActionForPermissionRequest(recentUserPermissionAction: RecentUserPermissionAction) {
        propertiesRepository.storeRecentActionForReadStoragePermission(recentUserPermissionAction)
    }

    override suspend fun getActionForPermissionRequest(): RecentUserPermissionAction {
        return propertiesRepository.getRecentActionForReadStoragePermission()
    }

    override suspend fun shouldShowPermissionRequest(systemShouldShow: RuntimePermissionRequest): RuntimePermissionRequest {
        val userPermissionAction = propertiesRepository.getRecentActionForReadStoragePermission()
        return combineReadStoragePermission(userPermissionAction, systemShouldShow)
    }

    private fun combineReadStoragePermission(
        recentUserPermissionAction: RecentUserPermissionAction,
        systemRuntimePermissionRequest: RuntimePermissionRequest
    ): RuntimePermissionRequest {
        if (recentUserPermissionAction != DENIED) {
            return ShouldShowReadStorage
        }

        if (systemRuntimePermissionRequest == ShouldShowReadStorage) {
            return ShouldShowReadStorage
        }

        return ShouldNotShowReadStorage
    }
}
