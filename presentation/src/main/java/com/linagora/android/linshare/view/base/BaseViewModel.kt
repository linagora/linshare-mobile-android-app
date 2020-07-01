package com.linagora.android.linshare.view.base

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.OperatorType.OfflineOperatorType
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Failure.CannotExecuteWithoutNetwork
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.NetworkConnectivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel(
    open val internetAvailable: ConnectionLiveData,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    companion object {
        val INITIAL_STATE = Either.Right(Success.Idle)
    }

    private val state = MutableLiveData<Either<Failure, Success>>()
        .apply { value = INITIAL_STATE }

    val viewState: LiveData<Either<Failure, Success>> = state

    @MainThread
    fun dispatchState(state: Either<Failure, Success>) {
        this.state.value = state
        onDispatchedState(state)
    }

    fun dispatchUIState(state: Either<Failure, Success>) {
        viewModelScope.launch(dispatcherProvider.main) {
            dispatchState(state)
        }
    }

    fun dispatchResetState() = dispatchState(Either.right(Success.Idle))

    suspend fun consumeStates(states: Flow<State<Either<Failure, Success>>>) {
        states.collect {
            withContext(dispatcherProvider.main) {
                dispatchState(it(state()))
            }
        }
    }

    suspend fun consumeStates(operatorType: OperatorType, statesGenerator: () -> Flow<State<Either<Failure, Success>>>) {
        if (!validateNetwork(operatorType)) {
            dispatchUIState(Either.left(CannotExecuteWithoutNetwork(operatorType)))
            return
        }
        consumeStates(statesGenerator())
    }

    private fun validateNetwork(operatorType: OperatorType): Boolean {
        if (internetAvailable.value == NetworkConnectivity.DISCONNECTED) {
            return operatorType is OfflineOperatorType
        }
        return true
    }

    private fun state() = state.value!!

    private fun onDispatchedState(state: Either<Failure, Success>) {
        when (state) {
            is Either.Right -> onSuccessDispatched(state.b)
            is Either.Left -> onFailureDispatched(state.a)
        }
    }

    protected open fun onSuccessDispatched(success: Success) {}
    protected open fun onFailureDispatched(failure: Failure) {}
}
