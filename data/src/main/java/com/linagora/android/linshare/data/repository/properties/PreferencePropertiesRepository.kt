/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.data.repository.properties

import android.content.SharedPreferences
import com.linagora.android.linshare.data.repository.properties.PreferencePropertiesRepository.Key.RECENT_ACTION_READ_CONTACT_PERMISSION_KEY
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

        const val RECENT_ACTION_READ_CONTACT_PERMISSION_KEY = "recent_action_read_contact_permission"
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

    override suspend fun storeRecentActionForReadContactPermission(previousUserPermissionAction: PreviousUserPermissionAction) {
        with(sharedPreferences.edit()) {
            val denied = when (previousUserPermissionAction) {
                PreviousUserPermissionAction.DENIED -> true
                else -> false
            }
            putBoolean(RECENT_ACTION_READ_CONTACT_PERMISSION_KEY, denied)
            commit()
        }
    }

    override suspend fun getRecentActionForReadContactPermission(): PreviousUserPermissionAction {
        if (sharedPreferences.getBoolean(RECENT_ACTION_READ_CONTACT_PERMISSION_KEY, false)) {
            return PreviousUserPermissionAction.DENIED
        }
        return PreviousUserPermissionAction.NONE
    }
}
