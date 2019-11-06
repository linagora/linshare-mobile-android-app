package com.linagora.android.linshare.view.accounts

import androidx.lifecycle.Observer
import arrow.core.Either
import com.linagora.android.linshare.CoroutinesExtension
import com.linagora.android.linshare.InstantExecutorExtension
import com.linagora.android.linshare.domain.usecases.account.GetAccountDetailsInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.linshare.runBlockingTest
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
            getAccountDetails = getAccountDetails,
            dispatcherProvider = provideFakeCoroutinesDispatcherProvider(coroutinesExtension.testDispatcher)
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
}
