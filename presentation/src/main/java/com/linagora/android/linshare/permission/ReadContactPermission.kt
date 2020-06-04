package com.linagora.android.linshare.permission

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import com.linagora.android.linshare.model.permission.PermissionName
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowReadContact
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowReadContact
import com.linagora.android.linshare.view.ReadContactPermissionRequestCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadContactPermission @Inject constructor(
    private val propertiesRepository: PropertiesRepository
) : Permission {
    override fun permissionName() = PermissionName(Manifest.permission.READ_CONTACTS)

    override fun requestCode() = ReadContactPermissionRequestCode

    override fun systemShouldShowPermissionRequest(activity: Activity): RuntimePermissionRequest {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName().name)) {
            return ShouldShowReadContact
        }
        return ShouldNotShowReadContact
    }

    override suspend fun setActionForPermissionRequest(previousUserPermissionAction: PreviousUserPermissionAction) {
        propertiesRepository.storeRecentActionForReadContactPermission(previousUserPermissionAction)
    }

    override suspend fun getActionForPermissionRequest(): PreviousUserPermissionAction {
        return propertiesRepository.getRecentActionForReadContactPermission()
    }

    override suspend fun shouldShowPermissionRequest(systemShouldShow: RuntimePermissionRequest): RuntimePermissionRequest {
        val userPermissionAction = propertiesRepository.getRecentActionForReadContactPermission()
        return combineReadContactPermission(userPermissionAction, systemShouldShow)
    }

    private fun combineReadContactPermission(
        previousUserPermissionAction: PreviousUserPermissionAction,
        systemRuntimePermissionRequest: RuntimePermissionRequest
    ): RuntimePermissionRequest {
        if (previousUserPermissionAction != PreviousUserPermissionAction.DENIED) {
            return ShouldShowReadContact
        }

        if (systemRuntimePermissionRequest == ShouldShowReadContact) {
            return ShouldShowReadContact
        }

        return ShouldNotShowReadContact
    }
}
