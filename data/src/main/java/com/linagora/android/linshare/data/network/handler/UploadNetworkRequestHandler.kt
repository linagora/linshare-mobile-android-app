package com.linagora.android.linshare.data.network.handler

import com.linagora.android.linshare.data.network.parseLinShareErrorResponse
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.INTERNET_NOT_AVAILABLE
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.UNKNOWN_RESPONSE
import com.linagora.android.linshare.domain.utils.OnCatch
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadNetworkRequestHandler @Inject constructor(
    private val retrofit: Retrofit
) : OnCatch {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadNetworkRequestHandler::class.java)
    }

    override fun invoke(throwable: Throwable) {
        LOGGER.error("invoke(): ${throwable.message} - ${throwable.printStackTrace()}")
        when (throwable) {
            is HttpException -> reactToHttpErrorResponse(throwable)
            is SocketException -> throw UploadException(INTERNET_NOT_AVAILABLE)
            is UnknownHostException -> throw UploadException(INTERNET_NOT_AVAILABLE)
            else -> throw UploadException(UNKNOWN_RESPONSE)
        }
    }

    private fun reactToHttpErrorResponse(httpException: HttpException) {
        val errorResponse = retrofit.parseLinShareErrorResponse(httpException)
        throw UploadException(errorResponse)
    }
}
