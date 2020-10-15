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

package com.linagora.android.linshare.data.repository.credential

import android.content.SharedPreferences
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.repository.CredentialRepository
import javax.inject.Inject

class PreferenceCredentialRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : CredentialRepository {

    object Key {

        const val SERVER_NAME = "serverName"

        const val USER_NAME = "userName"

        const val BACK_END_API_VERSION = "backEndApiVersion"
    }

    override suspend fun persistsCredential(credential: Credential) {
        with(sharedPreferences.edit()) {
            putString(Key.SERVER_NAME, credential.serverUrl.toString())
            putString(Key.USER_NAME, credential.userName.username)
            putString(Key.BACK_END_API_VERSION, credential.supportVersion.name)
            commit()
        }
    }

    override suspend fun removeCredential(credential: Credential) {
        if (containsCredential(credential)) {
            clearCredential()
        }
    }

    override suspend fun setCurrentCredential(credential: Credential): Boolean {
        return containsCredential(credential)
    }

    override suspend fun getCurrentCredential(): Credential? {
        return with(sharedPreferences) {
            val serverName = getString(Key.SERVER_NAME, null)
            val supportVersion = getString(Key.BACK_END_API_VERSION, null)
            val userName = getString(Key.USER_NAME, null)
            serverName?.takeIf { supportVersion != null && userName != null }
                ?.let { Credential.fromString(serverName, supportVersion!!, userName!!) }
        }
    }

    override suspend fun getAllCredential(): Set<Credential> {
        return getCurrentCredential()
            ?.let { setOf(it) }
            ?: emptySet()
    }

    override suspend fun clearCredential() {
        with(sharedPreferences.edit()) {
            remove(Key.SERVER_NAME)
            remove(Key.USER_NAME)
            commit()
        }
    }

    private fun containsCredential(credential: Credential): Boolean {
        with(sharedPreferences) {
            val serverName = getString(Key.SERVER_NAME, null)
            val userName = getString(Key.USER_NAME, null)

            serverName?.let {
                userName?.let {
                    if (credential.serverUrl.toString() == serverName &&
                        credential.userName.username == userName) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
