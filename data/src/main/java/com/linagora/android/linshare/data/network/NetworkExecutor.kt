package com.linagora.android.linshare.data.network

import com.linagora.android.linshare.domain.utils.NoOpOnCatch
import com.linagora.android.linshare.domain.utils.OnCatch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkExecutor @Inject constructor() {

    suspend fun <R> execute(
        networkRequest: suspend () -> R,
        onFailure: OnCatch = NoOpOnCatch
    ): R {
        return runCatching { networkRequest() }
            .onFailure(onFailure)
            .getOrThrow()
    }
}
