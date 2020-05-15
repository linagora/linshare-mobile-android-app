package com.linagora.android.linshare.domain.usecases.upload

import arrow.core.Either
import com.linagora.android.linshare.domain.model.BaseErrorCode
import com.linagora.android.linshare.domain.model.ClientErrorCode
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.network.InternetNotAvailable
import com.linagora.android.linshare.domain.usecases.quota.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.InternetNotAvailableErrorCode
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode
import com.linagora.android.linshare.domain.utils.OnCatch
import com.linagora.android.linshare.domain.utils.sendState
import kotlinx.coroutines.channels.ProducerScope
import org.slf4j.LoggerFactory

class UploadErrorHandler(
    private val producerScope: ProducerScope<State<Either<Failure, Success>>>
) : OnCatch {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadErrorHandler::class.java)
    }

    override fun invoke(throwable: Throwable) {
        LOGGER.error("invoke(): ${throwable.message} - ${throwable.printStackTrace()}")
        if (throwable is UploadException) {
            when (val uploadErrorCode = throwable.errorResponse.errCode) {
                is ClientErrorCode -> handleStateClientErrorCode(uploadErrorCode)
                is LinShareErrorCode -> handleStateLinShareErrorCode(uploadErrorCode)
                else -> producerScope.sendState { Either.left(Failure.Error) }
            }
        }
    }

    private fun handleStateClientErrorCode(uploadErrorCode: BaseErrorCode) {
        if (uploadErrorCode == InternetNotAvailableErrorCode) {
            producerScope.sendState { Either.left(InternetNotAvailable) }
        }
    }

    private fun handleStateLinShareErrorCode(uploadErrorCode: BaseErrorCode) {
        if (uploadErrorCode == QuotaAccountNoMoreSpaceErrorCode) {
            producerScope.sendState { Either.left(QuotaAccountNoMoreSpaceAvailable) }
        }
    }
}
