package com.linagora.android.linshare.domain.usecases.copy

import arrow.core.Either
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.usecases.myspace.CopyFailedWithFileSizeExceed
import com.linagora.android.linshare.domain.usecases.myspace.CopyFailedWithQuotaReach
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.FileSizeIsGreaterThanMaxFileSize
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode
import com.linagora.android.linshare.domain.utils.OnCatch
import com.linagora.android.linshare.domain.utils.sendState
import kotlinx.coroutines.channels.ProducerScope
import org.slf4j.LoggerFactory

class CopyErrorHandler(
    private val producerScope: ProducerScope<State<Either<Failure, Success>>>
) : OnCatch {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CopyErrorHandler::class.java)
    }

    override fun invoke(throwable: Throwable) {
        LOGGER.error("invoke(): ${throwable.message} - ${throwable.printStackTrace()}")
        if (throwable is CopyException) {
            when (val copyErrorCode = throwable.errorResponse.errCode) {
                is LinShareErrorCode -> handleLinShareErrorCode(copyErrorCode)
                else -> producerScope.sendState { Either.left(Failure.Error) }
            }
        }
    }

    private fun handleLinShareErrorCode(errorCode: LinShareErrorCode) {
        val state = when (errorCode) {
            QuotaAccountNoMoreSpaceErrorCode -> Either.left(CopyFailedWithQuotaReach)
            FileSizeIsGreaterThanMaxFileSize -> Either.left(CopyFailedWithFileSizeExceed)
            else -> Either.left(Failure.Error)
        }
        producerScope.sendState { state }
    }
}
