package com.linagora.android.linshare.domain.repository

import com.linagora.android.linshare.domain.model.properties.UserStoragePermissionRequest

interface PropertiesRepository {

    suspend fun storeDeniedStoragePermission(userStoragePermissionRequest: UserStoragePermissionRequest)

    suspend fun getDeniedStoragePermission(): UserStoragePermissionRequest
}
