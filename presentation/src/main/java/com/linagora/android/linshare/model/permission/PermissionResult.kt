package com.linagora.android.linshare.model.permission

import android.content.pm.PackageManager

sealed class PermissionResult(value: Int) {
    init {
        require(value == PackageManager.PERMISSION_GRANTED ||
            value == PackageManager.PERMISSION_DENIED) { "permission result is invalid" }
    }

    object PermissionGranted : PermissionResult(PackageManager.PERMISSION_GRANTED)

    object PermissionDenied : PermissionResult(PackageManager.PERMISSION_DENIED)
}
