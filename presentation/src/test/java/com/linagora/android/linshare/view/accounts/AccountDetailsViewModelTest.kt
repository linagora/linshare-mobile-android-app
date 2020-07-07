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

package com.linagora.android.linshare.view.accounts

import androidx.lifecycle.Observer
import arrow.core.Either
import com.linagora.android.linshare.CoroutinesExtension
import com.linagora.android.linshare.InstantExecutorExtension
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.usecases.account.GetAccountDetailsInteractor
import com.linagora.android.linshare.domain.usecases.auth.RemoveAccountInteractor
import com.linagora.android.linshare.domain.usecases.auth.SuccessRemoveAccount
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.runBlockingTest
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.utils.provideFakeCoroutinesDispatcherProvider
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.State.ACCOUNT_DETAILS_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.EMPTY_TOKEN_STATE
import com.linagora.android.testshared.TestFixtures.State.ERROR_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.flow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExtendWith(InstantExecutorExtension::class)
class AccountDetailsViewModelTest {

    @Mock
    lateinit var getAccountDetails: GetAccountDetailsInteractor

    @Mock
    lateinit var viewObserver: Observer<Either<Failure, Success>>

    @Mock
    lateinit var removeAccount: RemoveAccountInteractor

    @Mock
    lateinit var dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor

    @Mock
    lateinit var authorizationManager: AuthorizationManager

    @Mock
    lateinit var internetAvailable: ConnectionLiveData

    private lateinit var accountDetailsViewModel: AccountDetailsViewModel

    companion object {
        @JvmField
        @RegisterExtension
        val coroutinesExtension = CoroutinesExtension()
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        accountDetailsViewModel = AccountDetailsViewModel(
            internetAvailable = internetAvailable,
            getAccountDetails = getAccountDetails,
            dispatcherProvider = provideFakeCoroutinesDispatcherProvider(coroutinesExtension.testDispatcher),
            removeAccountInteractor = removeAccount,
            dynamicBaseUrlInterceptor = dynamicBaseUrlInterceptor,
            authorizationManager = authorizationManager
        )
    }

    @Test
    fun retrieveAccountDetailsShouldEmitAllStateWithRightCredential() {
        coroutinesExtension.runBlockingTest {
            `when`(getAccountDetails(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                        emitState { ACCOUNT_DETAILS_SUCCESS_STATE }
                    }
                }

            accountDetailsViewModel.viewState.observeForever(viewObserver)

            accountDetailsViewModel.retrieveAccountDetails(LINSHARE_CREDENTIAL)

            verify(viewObserver).onChanged(LOADING_STATE)
            verify(viewObserver).onChanged(AUTHENTICATE_SUCCESS_STATE)
            verify(viewObserver).onChanged(ACCOUNT_DETAILS_SUCCESS_STATE)
        }
    }

    @Test
    fun retrieveAccountDetailsShouldNotEmitEndStateWithWrongCredential() {
        coroutinesExtension.runBlockingTest {
            `when`(getAccountDetails(CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { EMPTY_TOKEN_STATE }
                    }
                }

            accountDetailsViewModel.viewState.observeForever(viewObserver)

            accountDetailsViewModel.retrieveAccountDetails(CREDENTIAL)

            verify(viewObserver).onChanged(LOADING_STATE)
            verify(viewObserver).onChanged(EMPTY_TOKEN_STATE)
        }
    }

    @Test
    fun retrieveAccountDetailsShouldEmitErrorStateWithGetLastLoginFailed() {
        coroutinesExtension.runBlockingTest {
            `when`(getAccountDetails(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                        emitState { ERROR_STATE }
                    }
                }

            accountDetailsViewModel.viewState.observeForever(viewObserver)

            accountDetailsViewModel.retrieveAccountDetails(LINSHARE_CREDENTIAL)

            verify(viewObserver).onChanged(LOADING_STATE)
            verify(viewObserver).onChanged(AUTHENTICATE_SUCCESS_STATE)
            verify(viewObserver).onChanged(ERROR_STATE)
        }
    }

    @Test
    fun removeAccountShouldSuccessWithRightCredential() {
        coroutinesExtension.runBlockingTest {
            `when`(removeAccount(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { Either.right(SuccessRemoveAccount) }
                    }
                }

            accountDetailsViewModel.viewState.observeForever(viewObserver)

            accountDetailsViewModel.removeAccount(LINSHARE_CREDENTIAL)

            verify(viewObserver).onChanged(LOADING_STATE)
            verify(viewObserver).onChanged(Either.right(SuccessRemoveAccount))
        }
    }

    @Test
    fun resetInterceptorsShouldResetAllRelatedInterceptors() {
        accountDetailsViewModel.resetInterceptors()

        verify(dynamicBaseUrlInterceptor).reset()
        verify(authorizationManager).reset()
    }
}
