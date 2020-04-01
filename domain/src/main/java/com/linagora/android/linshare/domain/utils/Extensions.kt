package com.linagora.android.linshare.domain.utils

import com.linagora.android.linshare.domain.usecases.utils.State
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> FlowCollector<State<T>>.emitState(f: T.() -> T) = emit(State(f))

inline fun <reified T> List<*>.asListOfType(): List<T>? =
    this.takeIf { all { it is T } }
        ?.let {
            @Suppress("UNCHECKED_CAST")
            it as List<T>
        }
