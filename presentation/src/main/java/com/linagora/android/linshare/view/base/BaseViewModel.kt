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
