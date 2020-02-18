package com.linagora.android.linshare.domain.repository

import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction

interface PropertiesRepository {

    suspend fun storeRecentActionForReadStoragePermission(previousUserPermissionAction: PreviousUserPermissionAction)

    suspend fun getRecentActionForReadStoragePermission(): PreviousUserPermissionAction

    suspend fun storeRecentActionForWriteStoragePermission(previousUserPermissionAction: PreviousUserPermissionAction)

    suspend fun getRecentActionForWriteStoragePermission(): PreviousUserPermissionAction
}
