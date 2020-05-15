package com.linagora.android.linshare.domain.usecases

import arrow.core.Either
import arrow.core.orNull
import com.linagora.android.linshare.domain.utils.DefaultOnCatch
import com.linagora.android.linshare.domain.utils.OnCatch
import javax.inject.Inject

class InteractorHandler @Inject constructor() {

    suspend fun <R> handle(
        execution: suspend () -> R,
        onCatch: OnCatch = DefaultOnCatch
    ): R? {
        return Either.catch { execution() }
            .mapLeft(onCatch)
            .orNull()
    }
}
