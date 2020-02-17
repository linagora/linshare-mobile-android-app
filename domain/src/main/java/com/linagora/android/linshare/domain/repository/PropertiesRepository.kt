package com.linagora.android.linshare.domain.repository

import com.linagora.android.linshare.domain.model.properties.RecentUserPermissionAction

interface PropertiesRepository {

    suspend fun storeRecentActionForReadStoragePermission(recentUserPermissionAction: RecentUserPermissionAction)

    suspend fun getRecentActionForReadStoragePermission(): RecentUserPermissionAction
}
