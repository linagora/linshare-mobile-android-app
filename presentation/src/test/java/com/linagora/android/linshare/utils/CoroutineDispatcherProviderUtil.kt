package com.linagora.android.linshare.utils

import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.test.TestCoroutineDispatcher

fun provideFakeCoroutinesDispatcherProvider(
    dispatcher: TestCoroutineDispatcher?
): CoroutinesDispatcherProvider {
    val sharedTestCoroutineDispatcher = TestCoroutineDispatcher()
    return CoroutinesDispatcherProvider(
        dispatcher ?: sharedTestCoroutineDispatcher,
        dispatcher ?: sharedTestCoroutineDispatcher,
        dispatcher ?: sharedTestCoroutineDispatcher)
}
