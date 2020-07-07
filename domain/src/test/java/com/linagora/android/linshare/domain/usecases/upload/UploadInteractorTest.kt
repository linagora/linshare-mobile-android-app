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

package com.linagora.android.linshare.domain.usecases.upload

import android.os.Build
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.DomainFixtures.BIG_FILE_SIZE
import com.linagora.android.linshare.domain.DomainFixtures.DOCUMENT_REQUEST
import com.linagora.android.linshare.domain.DomainFixtures.DOCUMENT_REQUEST_BIG_SIZE
import com.linagora.android.linshare.domain.DomainFixtures.FILE_SIZE
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.InteractorHandler
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.EXCEED_MAX_FILE_SIZE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.INTERNET_NOT_AVAILABLE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE
import com.linagora.android.testshared.TestFixtures.State.UPLOAD_SUCCESS_VIEW_STATE
import com.linagora.android.testshared.TestFixtures.State.VALID_QUOTA_ACCOUNT_STATE
import com.linagora.android.testshared.TestFixtures.State.WRONG_CREDENTIAL_STATE
import com.linagora.android.testshared.extension.MockitoUtils.any
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UploadInteractorTest {

    @Mock
    lateinit var getAuthenticatedInfoInteractor: GetAuthenticatedInfoInteractor

    @Mock
    lateinit var enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor

    @Mock
    lateinit var documentRepository: DocumentRepository

    private lateinit var interactorHandler: InteractorHandler

    private lateinit var uploadInteractor: UploadInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactorHandler = InteractorHandler()
        uploadInteractor = UploadInteractor(
            getAuthenticatedInfoInteractor,
            enoughAccountQuotaInteractor,
            documentRepository,
            interactorHandler,
            ViewStateStore()
        )
    }

    @Test
    fun uploadShouldFailedWhenInternetNotAvailableDuringUpload() {
        runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(enoughAccountQuotaInteractor(FILE_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { INTERNET_NOT_AVAILABLE }
                    }
                }

            val states = uploadInteractor(DOCUMENT_REQUEST)
                .toList(ArrayList())

            assertThat(states).hasSize(3)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(INTERNET_NOT_AVAILABLE)
        }
    }

    @Test
    fun uploadShouldFailedWhenPreCheckExceedMaxFileSize() {
        runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(enoughAccountQuotaInteractor(FILE_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { EXCEED_MAX_FILE_SIZE }
                    }
                }

            val states = uploadInteractor(DOCUMENT_REQUEST)
                .toList(ArrayList())

            assertThat(states).hasSize(3)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(EXCEED_MAX_FILE_SIZE)
        }
    }

    @Test
    fun uploadShouldFailedWhenPreCheckNoMoreSpaceAvailable() {
        runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(enoughAccountQuotaInteractor(BIG_FILE_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE }
                    }
                }

            val states = uploadInteractor(DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(3)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE)
        }
    }

    @Test
    fun uploadShouldFailedWhenNotEnoughQuota() {
        runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(enoughAccountQuotaInteractor(BIG_FILE_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { VALID_QUOTA_ACCOUNT_STATE }
                    }
                }

            `when`(documentRepository.upload(any(), any()))
                .thenThrow(UploadException(
                    ErrorResponse("quota exceeded", BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode)
                ))

            val states = uploadInteractor(DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(4)

            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(VALID_QUOTA_ACCOUNT_STATE)

            assertThat(states[3](VALID_QUOTA_ACCOUNT_STATE))
                .isEqualTo(QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE)
        }
    }

    @Test
    fun uploadShouldSFailedWhenWrongCredential() {
        runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { WRONG_CREDENTIAL_STATE }
                    }
                }

            `when`(enoughAccountQuotaInteractor(BIG_FILE_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { VALID_QUOTA_ACCOUNT_STATE }
                    }
                }

            `when`(documentRepository.upload(any(), any()))
                .thenAnswer { DOCUMENT }

            val states = uploadInteractor(DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun uploadShouldSuccess() {
        runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(enoughAccountQuotaInteractor(BIG_FILE_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { VALID_QUOTA_ACCOUNT_STATE }
                    }
                }

            `when`(documentRepository.upload(any(), any()))
                .thenAnswer { DOCUMENT }

            val states = uploadInteractor(DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(4)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(VALID_QUOTA_ACCOUNT_STATE)

            assertThat(states[3](VALID_QUOTA_ACCOUNT_STATE))
                .isEqualTo(UPLOAD_SUCCESS_VIEW_STATE)
        }
    }
}
