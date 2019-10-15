package com.linagora.android.linshare.domain.utils

import com.linagora.android.linshare.domain.usecases.utils.State
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> FlowCollector<State<T>>.emitState(f: T.() -> T) = emit(State(f))
