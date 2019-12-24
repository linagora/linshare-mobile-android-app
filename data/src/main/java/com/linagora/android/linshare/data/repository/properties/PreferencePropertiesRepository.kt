package com.linagora.android.linshare.data.repository.properties

import android.content.SharedPreferences
import arrow.core.Either
import arrow.core.getOrElse
import com.linagora.android.linshare.data.repository.properties.PreferencePropertiesRepository.Key.DENIED_STORAGE_PERMISSION_KEY
import com.linagora.android.linshare.domain.model.properties.UserStoragePermissionRequest
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import javax.inject.Inject

class PreferencePropertiesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PropertiesRepository {

    object Key {

        const val DENIED_STORAGE_PERMISSION_KEY = "denied_storage_permission"
    }

    override suspend fun storeDeniedStoragePermission(userStoragePermissionRequest: UserStoragePermissionRequest) {
        with(sharedPreferences.edit()) {
            val denied = when (userStoragePermissionRequest) {
                UserStoragePermissionRequest.DENIED -> true
                else -> false
            }
            putBoolean(DENIED_STORAGE_PERMISSION_KEY, denied)
            commit()
        }
    }

    override suspend fun getDeniedStoragePermission(): UserStoragePermissionRequest {
        return Either.cond(
            test = sharedPreferences.getBoolean(DENIED_STORAGE_PERMISSION_KEY, false),
            ifTrue = { UserStoragePermissionRequest.DENIED },
            ifFalse = { UserStoragePermissionRequest.NONE }
        ).getOrElse { UserStoragePermissionRequest.NONE }
    }
}
