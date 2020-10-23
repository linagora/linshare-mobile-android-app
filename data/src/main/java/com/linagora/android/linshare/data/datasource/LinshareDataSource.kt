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

package com.linagora.android.linshare.data.datasource

import android.content.Context
import android.provider.Settings
import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.model.authentication.PermanentTokenBodyRequest
import com.linagora.android.linshare.data.network.NetworkExecutor
import com.linagora.android.linshare.data.network.handler.AuthenticationRequestHandler
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.LastLogin
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.User
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.network.Endpoint
import com.linagora.android.linshare.domain.network.SupportVersion
import com.linagora.android.linshare.domain.network.withServicePath
import com.linagora.android.linshare.domain.network.withSupportVersion
import okhttp3.Credentials
import java.net.URL
import javax.inject.Inject

class LinshareDataSource @Inject constructor(
    private val context: Context,
    private val linshareApi: LinshareApi,
    private val networkExecutor: NetworkExecutor,
    private val authenticationRequestHandler: AuthenticationRequestHandler
) {

    suspend fun retrievePermanentToken(baseUrl: URL, supportVersion: SupportVersion, username: Username, password: Password): Token {
        return networkExecutor.execute(
            networkRequest = { createPermanentToken(baseUrl, supportVersion, username, password) },
            onFailure = { authenticationRequestHandler.handle(it, supportVersion) }
        )
    }

    private suspend fun createPermanentToken(baseUrl: URL, supportVersion: SupportVersion, username: Username, password: Password): Token {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return linshareApi.createPermanentToken(
            authenticateUrl = baseUrl.withSupportVersion(supportVersion)
                .withServicePath(Endpoint.AUTHENTICATION_PATH).toString(),
            basicToken = Credentials.basic(username.username, password.value),
            permanentToken = PermanentTokenBodyRequest(label = "LinShare-Android-App-$androidId")
        )
    }

    suspend fun deletePermanentToken(token: Token): Boolean {
        return runCatching {
            linshareApi.deletePermanentToken(token.uuid.toString())
            return true
        }.getOrDefault(false)
    }

    suspend fun getLastLogin(): LastLogin? {
        return runCatching {
            LastLogin(
                linshareApi.auditAuthenticationEntryUser()
                    .last()
                    .creationDate
            )
        }.getOrNull()
    }

    suspend fun getAuthorizedUser(): User? {
        return runCatching {
            linshareApi.getAuthorizedUser()
        }.getOrNull()
    }

    suspend fun findQuota(quotaUuid: QuotaId): AccountQuota? {
        return kotlin.runCatching {
            linshareApi.getQuota(quotaUuid.uuid.toString())
        }.getOrNull()
    }
}
