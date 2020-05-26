package com.linagora.android.linshare.data.network.handler

import com.linagora.android.linshare.data.network.parseLinShareErrorResponse
import com.linagora.android.linshare.domain.usecases.copy.CopyException
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.UNKNOWN_RESPONSE
import com.linagora.android.linshare.domain.utils.OnCatch
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CopyNetworkRequestHandler @Inject constructor(
    private val retrofit: Retrofit
) : OnCatch {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CopyNetworkRequestHandler::class.java)
    }

    override fun invoke(throwable: Throwable) {
        LOGGER.error("invoke(): ${throwable.message} - ${throwable.printStackTrace()}")
        when (throwable) {
            is HttpException -> throw CopyException(retrofit.parseLinShareErrorResponse(throwable))
            else -> throw CopyException(UNKNOWN_RESPONSE)
        }
    }
}
