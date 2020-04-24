package com.linagora.android.linshare.domain.usecases.utils

import arrow.core.Either
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class ViewStateStore @Inject constructor() {
    private val currentState = AtomicReference<Either<Failure, Success>>(Either.right(Success.Idle))

    fun getCurrentState(): Either<Failure, Success> {
        return currentState.get()
    }

    fun storeAndGet(state: State<Either<Failure, Success>>): Either<Failure, Success> {
        val newState = state(currentState.get())
        currentState.set(newState)
        return newState
    }
}
