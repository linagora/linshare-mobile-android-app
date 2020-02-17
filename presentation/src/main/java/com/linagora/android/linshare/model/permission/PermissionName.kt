package com.linagora.android.linshare.model.permission

data class PermissionName(val name: String) {
    init {
        require(name.isNotBlank()) { "permission name must be a value from Manifest.permission" }
    }
}
