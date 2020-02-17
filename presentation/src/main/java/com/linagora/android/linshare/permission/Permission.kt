package com.linagora.android.linshare.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.linagora.android.linshare.domain.model.properties.RecentUserPermissionAction
import com.linagora.android.linshare.model.permission.PermissionName
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.view.PermissionRequestCode

interface Permission {

    fun permissionName(): PermissionName

    fun requestCode(): PermissionRequestCode

    fun checkSelfPermission(context: Context): PermissionResult {
        return when (ContextCompat.checkSelfPermission(context, permissionName().name)) {
            PackageManager.PERMISSION_GRANTED -> PermissionResult.PermissionGranted
            else -> PermissionResult.PermissionDenied
        }
    }

    fun systemShouldShowPermissionRequest(activity: Activity): RuntimePermissionRequest

    suspend fun setActionForPermissionRequest(recentUserPermissionAction: RecentUserPermissionAction)

    suspend fun getActionForPermissionRequest(): RecentUserPermissionAction

    suspend fun shouldShowPermissionRequest(systemShouldShow: RuntimePermissionRequest): RuntimePermissionRequest

    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permissionName().name),
            requestCode().code
        )
    }
}
