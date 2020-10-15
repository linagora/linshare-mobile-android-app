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
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.ConnectError
import com.linagora.android.linshare.domain.usecases.auth.EmptyToken
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFoundException
import com.linagora.android.linshare.domain.usecases.auth.UnknownError
import kotlinx.coroutines.TimeoutCancellationException
import okhttp3.Credentials
import retrofit2.Response
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import javax.inject.Inject

class LinshareDataSource @Inject constructor(
    private val context: Context,
    private val linshareApi: LinshareApi
) {

    suspend fun retrievePermanentToken(baseUrl: URL, supportVersion: SupportVersion, username: Username, password: Password): Token {
        try {
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val response = linshareApi.createPermanentToken(
                authenticateUrl = baseUrl.withSupportVersion(supportVersion)
                    .withServicePath(Endpoint.AUTHENTICATION_PATH).toString(),
                basicToken = Credentials.basic(username.username, password.value),
                permanentToken = PermanentTokenBodyRequest(label = "LinShare-Android-App-$androidId")
            )
            return when (response.isSuccessful) {
                true -> response.body() ?: throw EmptyToken
                else -> throw produceError(response, supportVersion)
            }
        } catch (exp: Exception) {
            exp.printStackTrace()
            when (exp) {
                is EmptyToken -> throw exp
                is ServerNotFoundException -> throw exp
                is BadCredentials -> throw exp
                is SocketTimeoutException -> throw ConnectError
                is SocketException -> throw ConnectError
                is TimeoutCancellationException -> throw ConnectError
                is UnknownHostException -> throw ConnectError
                else -> throw UnknownError
            }
        }
    }

    private fun produceError(response: Response<Token>, supportVersion: SupportVersion): AuthenticationException {
        return when (response.code()) {
            404 -> ServerNotFoundException(supportVersion)
            401 -> BadCredentials(WRONG_CREDENTIAL)
            else -> UnknownError
        }
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
