package com.linagora.android.linshare.domain.utils

import com.linagora.android.linshare.domain.usecases.utils.State
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

suspend fun <T> FlowCollector<State<T>>.emitState(f: T.() -> T) = emit(State(f))

fun <T> ProducerScope<State<T>>.sendState(f: T.() -> T) = launch { send(State(f)) }

inline fun <reified T> List<*>.asListOfType(): List<T>? =
    this.takeIf { all { it is T } }
        ?.let {
            @Suppress("UNCHECKED_CAST")
            it as List<T>
        }

typealias OnCatch = (Throwable) -> Unit

object NoOpOnCatch : OnCatch {
    override fun invoke(throwable: Throwable) {
        // do nothing
    }
}

object DefaultOnCatch : OnCatch {
    override fun invoke(throwable: Throwable) {
        throw throwable
    }
}
