package com.linagora.android.linshare.data.repository.properties

import android.content.SharedPreferences
import com.linagora.android.linshare.data.repository.properties.PreferencePropertiesRepository.Key.DENIED_STORAGE_PERMISSION_KEY
import com.linagora.android.linshare.domain.model.properties.RecentUserPermissionAction
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import javax.inject.Inject

class PreferencePropertiesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PropertiesRepository {

    object Key {

        const val DENIED_STORAGE_PERMISSION_KEY = "denied_storage_permission"
    }

    override suspend fun storeRecentActionForReadStoragePermission(recentUserPermissionAction: RecentUserPermissionAction) {
        with(sharedPreferences.edit()) {
            val denied = when (recentUserPermissionAction) {
                RecentUserPermissionAction.DENIED -> true
                else -> false
            }
            putBoolean(DENIED_STORAGE_PERMISSION_KEY, denied)
            commit()
        }
    }

    override suspend fun getRecentActionForReadStoragePermission(): RecentUserPermissionAction {
        if (sharedPreferences.getBoolean(DENIED_STORAGE_PERMISSION_KEY, false)) {
            return RecentUserPermissionAction.DENIED
        }
        return RecentUserPermissionAction.NONE
    }
}
