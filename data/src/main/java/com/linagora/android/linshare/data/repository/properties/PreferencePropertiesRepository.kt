package com.linagora.android.linshare.data.repository.properties

import android.content.SharedPreferences
import com.linagora.android.linshare.data.repository.properties.PreferencePropertiesRepository.Key.RECENT_ACTION_READ_STORAGE_PERMISSION_KEY
import com.linagora.android.linshare.data.repository.properties.PreferencePropertiesRepository.Key.RECENT_ACTION_WRITE_STORAGE_PERMISSION_KEY
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import javax.inject.Inject

class PreferencePropertiesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PropertiesRepository {

    object Key {

        const val RECENT_ACTION_READ_STORAGE_PERMISSION_KEY = "recent_action_read_storage_permission"

        const val RECENT_ACTION_WRITE_STORAGE_PERMISSION_KEY = "recent_action_write_storage_permission"
    }

    override suspend fun storeRecentActionForReadStoragePermission(previousUserPermissionAction: PreviousUserPermissionAction) {
        with(sharedPreferences.edit()) {
            val denied = when (previousUserPermissionAction) {
                PreviousUserPermissionAction.DENIED -> true
                else -> false
            }
            putBoolean(RECENT_ACTION_READ_STORAGE_PERMISSION_KEY, denied)
            commit()
        }
    }

    override suspend fun getRecentActionForReadStoragePermission(): PreviousUserPermissionAction {
        if (sharedPreferences.getBoolean(RECENT_ACTION_READ_STORAGE_PERMISSION_KEY, false)) {
            return PreviousUserPermissionAction.DENIED
        }
        return PreviousUserPermissionAction.NONE
    }

    override suspend fun storeRecentActionForWriteStoragePermission(previousUserPermissionAction: PreviousUserPermissionAction) {
        with(sharedPreferences.edit()) {
            val denied = when (previousUserPermissionAction) {
                PreviousUserPermissionAction.DENIED -> true
                else -> false
            }
            putBoolean(RECENT_ACTION_WRITE_STORAGE_PERMISSION_KEY, denied)
            commit()
        }
    }

    override suspend fun getRecentActionForWriteStoragePermission(): PreviousUserPermissionAction {
        if (sharedPreferences.getBoolean(RECENT_ACTION_WRITE_STORAGE_PERMISSION_KEY, false)) {
            return PreviousUserPermissionAction.DENIED
        }
        return PreviousUserPermissionAction.NONE
    }
}
