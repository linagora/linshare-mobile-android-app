package com.linagora.android.linshare.domain.repository

import com.linagora.android.linshare.domain.model.properties.UserStoragePermissionHistory

interface PropertiesRepository {

    suspend fun storeDeniedStoragePermission(userStoragePermissionHistory: UserStoragePermissionHistory)

    suspend fun getDeniedStoragePermission(): UserStoragePermissionHistory
}
