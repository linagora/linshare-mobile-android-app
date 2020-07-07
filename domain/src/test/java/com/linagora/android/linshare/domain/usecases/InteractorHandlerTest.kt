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

package com.linagora.android.linshare.domain.usecases

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.usecases.upload.UploadErrorHandler
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.FILE_NOT_FOUND
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class InteractorHandlerTest {

    companion object {
        const val WRONG_VALUE_REQUEST = 7

        const val VALID_VALUE_REQUEST = 3
    }

    @Mock
    lateinit var producerScope: ProducerScope<State<Either<Failure, Success>>>

    private lateinit var interactorHandler: InteractorHandler

    private fun fakeInteract(value: Int): Int {
        require(value < 5) { "invalid value" }
        return value
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactorHandler = InteractorHandler()
    }

    @Nested
    inner class CommonCases {

        @Test
        fun handleShouldReturnASuccessRequest() = runBlockingTest {
            assertThat(interactorHandler.handle(
                    execution = { fakeInteract(VALID_VALUE_REQUEST) }))
                .isEqualTo(VALID_VALUE_REQUEST)
        }

        @Test
        fun handleShouldThrownAnExceptionOnFailure() {
            assertThrows<IllegalArgumentException> {
                runBlockingTest { interactorHandler.handle(execution = { fakeInteract(7) }) }
            }
        }

        @Test
        fun handleShouldThrownAnExceptionWhenThrowExceptionOnFailure() {
            assertThrows<RuntimeException> { runBlockingTest {
                interactorHandler.handle(
                    execution = { fakeInteract(WRONG_VALUE_REQUEST) },
                    onCatch = {
                        if (it is IllegalArgumentException) {
                            throw RuntimeException("failed request")
                        }
                    })
            } }
        }
    }

    @Nested
    inner class UploadCases {

        private lateinit var uploadErrorHandler: UploadErrorHandler

        private fun fakeUpload(value: Int): Int {
            if (value > 5) {
                throw UploadException(FILE_NOT_FOUND)
            }
            return value
        }

        @Test
        fun handleShouldReturnASuccessRequestWhenUploadSuccessWithUploadErrorHandler() = runBlockingTest {
            produce<State<Either<Failure, Success>>> {
                uploadErrorHandler = UploadErrorHandler(this)
            }

            assertThat(interactorHandler.handle(
                    execution = { fakeUpload(VALID_VALUE_REQUEST) },
                    onCatch = { uploadErrorHandler(it) }))
                .isEqualTo(VALID_VALUE_REQUEST)
        }

        @Test
        fun handleShouldBeHandleOnThrowWhenUploadSuccessWithUploadErrorHandler() = runBlockingTest {
            val states = channelFlow<State<Either<Failure, Success>>> {
                uploadErrorHandler = UploadErrorHandler(this)

                interactorHandler.handle(
                    execution = { fakeUpload(WRONG_VALUE_REQUEST) },
                    onCatch = { uploadErrorHandler(it) }
                )
            }.toList(ArrayList())

            assertThat(states).hasSize(1)
            assertThat(states[0](INIT_STATE)).isEqualTo(Either.left(Failure.Error))
        }
    }
}
