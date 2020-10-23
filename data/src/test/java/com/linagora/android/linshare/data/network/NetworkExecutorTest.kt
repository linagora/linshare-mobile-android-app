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

package com.linagora.android.linshare.data.network

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.network.handler.CommonNetworkRequestHandler
import com.linagora.android.linshare.domain.network.Endpoint
import com.linagora.android.linshare.domain.usecases.auth.Invalid2FactorAuthException
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Response

class NetworkExecutorTest {

    private lateinit var networkExecutor: NetworkExecutor

    private lateinit var commonNetworkRequestHandler: CommonNetworkRequestHandler

    private fun fakeRequest(value: Int): Int {
        require(value < 5) { "invalid value" }
        return value
    }

    private fun fakeRequestWithInvalidSecondFactorAuthCodeError(value: Int): Int {
        require(value < 5) { "invalid value" }
        return value.takeIf { value < 3 }
            ?: throw HttpException(
                Response.error<Int>(
                    ResponseBody.create(MediaType.get("text/plain"), "fake request"),
                    okhttp3.Response.Builder()
                        .code(401)
                        .request(Request.Builder().url("http://localhost").build())
                        .protocol(Protocol.HTTP_1_1)
                        .message("Fake request")
                        .addHeader(Endpoint.HeaderAuthErrorCode, BusinessErrorCode.InvalidTOTPCode.value.toString())
                        .build()))
    }

    @BeforeEach
    fun setUp() {
        commonNetworkRequestHandler = CommonNetworkRequestHandler()
        networkExecutor = NetworkExecutor(commonNetworkRequestHandler)
    }

    @Test
    fun executeShouldReturnASuccessRequest() = runBlockingTest {
        assertThat(networkExecutor.execute(
                networkRequest = { fakeRequest(3) },
                onFailure = { if (it is IllegalArgumentException) throw RuntimeException("failed request") }))
            .isEqualTo(3)
    }

    @Test
    fun executeShouldThrowAnExceptionWhenRequestHaveInvalidSecondFactorAuthCode() {
        assertThrows<Invalid2FactorAuthException> { runBlockingTest {
            networkExecutor.execute(
                networkRequest = { fakeRequestWithInvalidSecondFactorAuthCodeError(4) },
                onFailure = { if (it is IllegalArgumentException) throw RuntimeException("failed request") }
            )
        } }
    }

    @Test
    fun executeShouldThrowAnExceptionWhenRequestHaveAnErrorNotInCommonHandler() {
        assertThrows<RuntimeException> { runBlockingTest {
            networkExecutor.execute(
                networkRequest = { fakeRequestWithInvalidSecondFactorAuthCodeError(7) },
                onFailure = { if (it is IllegalArgumentException) throw RuntimeException("failed request") }
            )
        } }
    }

    @Test
    fun executeShouldThrownAnExceptionOnFailure() {
        assertThrows<IllegalArgumentException> { runBlockingTest {
            networkExecutor.execute(networkRequest = { fakeRequest(7) })
        } }
    }

    @Test
    fun executeShouldThrownAnExceptionWhenThrowExceptionOnFailure() {
        assertThrows<RuntimeException> { runBlockingTest {
            networkExecutor.execute(
                networkRequest = { fakeRequest(7) },
                onFailure = { if (it is IllegalArgumentException) throw RuntimeException("failed request") }
            ) }
        }
    }
}
