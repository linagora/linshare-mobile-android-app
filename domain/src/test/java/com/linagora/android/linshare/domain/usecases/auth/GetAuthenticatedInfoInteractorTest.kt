package com.linagora.android.linshare.domain.usecases.auth

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.linshare.domain.usecases.account.GetTokenInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.EMPTY_TOKEN_STATE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.WRONG_CREDENTIAL_STATE
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetAuthenticatedInfoInteractorTest {

    @Mock
    private lateinit var credentialRepository: CredentialRepository

    @Mock
    private lateinit var getTokenInteractor: GetTokenInteractor

    private lateinit var getAuthenticatedInfoInteractor: GetAuthenticatedInfoInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getAuthenticatedInfoInteractor = GetAuthenticatedInfoInteractor(
            credentialRepository = credentialRepository,
            getToken = getTokenInteractor
        )
    }

    @Test
    fun getAuthenticatedShouldSuccessWithCurrentCredential() {
        runBlockingTest {
            `when`(credentialRepository.getCurrentCredential())
                .thenAnswer { LINSHARE_CREDENTIAL }
            `when`(getTokenInteractor.invoke(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            val states = getAuthenticatedInfoInteractor()
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE)).isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE)).isEqualTo(AUTHENTICATE_SUCCESS_STATE)
        }
    }

    @Test
    fun getAuthenticatedShouldFailedWhenCurrentCredentialNotExist() {
        runBlockingTest {
            `when`(credentialRepository.getCurrentCredential())
                .thenAnswer { null }

            val states = getAuthenticatedInfoInteractor()
                .toList(ArrayList())

            assertThat(states).hasSize(1)
            assertThat(states[0](INIT_STATE)).isEqualTo(WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun getAuthenticatedShouldFailedWhenTokenNotExist() {
        runBlockingTest {
            `when`(credentialRepository.getCurrentCredential())
                .thenAnswer { LINSHARE_CREDENTIAL }
            `when`(getTokenInteractor.invoke(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { EMPTY_TOKEN_STATE }
                    }
                }

            val states = getAuthenticatedInfoInteractor()
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE)).isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE)).isEqualTo(EMPTY_TOKEN_STATE)
        }
    }
}
