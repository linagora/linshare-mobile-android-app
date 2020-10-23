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

package com.linagora.android.linshare.data.network.handler

import com.linagora.android.linshare.domain.network.SupportVersion
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.ConnectError
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFoundException
import com.linagora.android.linshare.domain.usecases.auth.UnknownError
import com.linagora.android.linshare.domain.utils.OnCatch
import kotlinx.coroutines.TimeoutCancellationException
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRequestHandler @Inject constructor() : OnCatch {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AuthenticationRequestHandler::class.java)
    }

    override fun invoke(throwable: Throwable) {
        handle(throwable, SupportVersion.Version4)
    }

    fun handle(throwable: Throwable, supportVersion: SupportVersion) {
        LOGGER.error("invoke(): ${throwable.message} - ${throwable.printStackTrace()}")
        when (throwable) {
            is HttpException -> handleToHttpErrorResponse(throwable, supportVersion)
            is BadCredentials -> throw throwable
            is SocketTimeoutException -> throw ConnectError
            is SocketException -> throw ConnectError
            is TimeoutCancellationException -> throw ConnectError
            is UnknownHostException -> throw ConnectError
            else -> throw UnknownError
        }
    }

    private fun handleToHttpErrorResponse(httpException: HttpException, supportVersion: SupportVersion) {
        val response = httpException.response()
        val exception = response
            ?.let {
                when (response.code()) {
                    401 -> BadCredentials(WRONG_CREDENTIAL)
                    404 -> ServerNotFoundException(supportVersion)
                    else -> UnknownError
                } }
            ?: UnknownError
        throw exception
    }
}
