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

package com.linagora.android.linshare.view

import androidx.lifecycle.Observer
import arrow.core.Either
import com.linagora.android.linshare.CoroutinesExtension
import com.linagora.android.linshare.InstantExecutorExtension
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.permission.ReadContactPermission
import com.linagora.android.linshare.permission.WriteStoragePermission
import com.linagora.android.linshare.runBlockingTest
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.utils.provideFakeCoroutinesDispatcherProvider
import com.linagora.android.testshared.TestFixtures
import kotlinx.coroutines.flow.flow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExtendWith(InstantExecutorExtension::class)
class MainActivityViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val coroutinesExtension = CoroutinesExtension()
    }

    @Mock
    lateinit var getAuthenticatedInfoInteractor: GetAuthenticatedInfoInteractor

    @Mock
    lateinit var dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor

    @Mock
    lateinit var authorizationManager: AuthorizationManager

    @Mock
    lateinit var viewObserver: Observer<Either<Failure, Success>>

    @Mock
    lateinit var writeStoragePermission: WriteStoragePermission

    @Mock
    lateinit var readContactPermission: ReadContactPermission

    @Mock
    lateinit var internetAvailable: ConnectionLiveData

    private lateinit var viewModel: MainActivityViewModel

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel =
            MainActivityViewModel(
                internetAvailable = internetAvailable,
                getAuthenticatedInfo = getAuthenticatedInfoInteractor,
                dispatcherProvider = provideFakeCoroutinesDispatcherProvider(coroutinesExtension.testDispatcher),
                dynamicBaseUrlInterceptor = dynamicBaseUrlInterceptor,
                authorizationManager = authorizationManager,
                writeStoragePermission = writeStoragePermission,
                readContactPermission = readContactPermission
            )
    }

    @Test
    fun checkSignedInShouldProduceSuccessState() {
        coroutinesExtension.runBlockingTest {
            Mockito.`when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { TestFixtures.State.LOADING_STATE }
                        emitState { TestFixtures.State.AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            viewModel.viewState.observeForever(viewObserver)

            viewModel.checkSignedIn()
            Mockito.verify(viewObserver).onChanged(TestFixtures.State.LOADING_STATE)
            Mockito.verify(viewObserver).onChanged(TestFixtures.State.AUTHENTICATE_SUCCESS_STATE)
        }
    }

    @Test
    fun checkSignedInShouldProduceWrongCredentialStateWhenCredentialNotExist() {
        coroutinesExtension.runBlockingTest {
            Mockito.`when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { TestFixtures.State.WRONG_CREDENTIAL_STATE }
                    }
                }

            viewModel.viewState.observeForever(viewObserver)

            viewModel.checkSignedIn()
            Mockito.verify(viewObserver).onChanged(TestFixtures.State.WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun checkSignedInShouldProduceEmptyTokenStateWhenTokenNotExist() {
        coroutinesExtension.runBlockingTest {
            Mockito.`when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { TestFixtures.State.LOADING_STATE }
                        emitState { TestFixtures.State.EMPTY_TOKEN_STATE }
                    }
                }

            viewModel.viewState.observeForever(viewObserver)

            viewModel.checkSignedIn()
            Mockito.verify(viewObserver).onChanged(TestFixtures.State.LOADING_STATE)
            Mockito.verify(viewObserver).onChanged(TestFixtures.State.EMPTY_TOKEN_STATE)
        }
    }
}
