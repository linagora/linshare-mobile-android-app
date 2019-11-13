package com.linagora.android.linshare.view.main

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
import com.linagora.android.linshare.runBlockingTest
import com.linagora.android.linshare.utils.provideFakeCoroutinesDispatcherProvider
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.EMPTY_TOKEN_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.WRONG_CREDENTIAL_STATE
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
class MainFragmentViewModelTest {

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

    private lateinit var mainFragmentViewModel: MainFragmentViewModel

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mainFragmentViewModel =
            MainFragmentViewModel(
                getAuthenticatedInfo = getAuthenticatedInfoInteractor,
                dispatcherProvider = provideFakeCoroutinesDispatcherProvider(coroutinesExtension.testDispatcher),
                dynamicBaseUrlInterceptor = dynamicBaseUrlInterceptor,
                authorizationManager = authorizationManager
            )
    }

    @Test
    fun checkSignedInShouldProduceSuccessState() {
        coroutinesExtension.runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            mainFragmentViewModel.viewState.observeForever(viewObserver)

            mainFragmentViewModel.checkSignedIn()
            `verify`(viewObserver).onChanged(LOADING_STATE)
            `verify`(viewObserver).onChanged(AUTHENTICATE_SUCCESS_STATE)
        }
    }

    @Test
    fun checkSignedInShouldProduceWrongCredentialStateWhenCredentialNotExist() {
        coroutinesExtension.runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { WRONG_CREDENTIAL_STATE }
                    }
                }

            mainFragmentViewModel.viewState.observeForever(viewObserver)

            mainFragmentViewModel.checkSignedIn()
            `verify`(viewObserver).onChanged(WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun checkSignedInShouldProduceEmptyTokenStateWhenTokenNotExist() {
        coroutinesExtension.runBlockingTest {
            `when`(getAuthenticatedInfoInteractor())
                .thenAnswer {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { EMPTY_TOKEN_STATE }
                    }
                }

            mainFragmentViewModel.viewState.observeForever(viewObserver)

            mainFragmentViewModel.checkSignedIn()
            `verify`(viewObserver).onChanged(LOADING_STATE)
            `verify`(viewObserver).onChanged(EMPTY_TOKEN_STATE)
        }
    }
}
