package com.linagora.android.linshare.data.network.handler

import com.linagora.android.linshare.data.network.parseLinShareErrorResponse
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.linshare.domain.utils.OnCatch
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject

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
            is SocketException -> throw UploadException(ErrorResponse.INTERNET_NOT_AVAILABLE)
            is UnknownHostException -> throw UploadException(ErrorResponse.INTERNET_NOT_AVAILABLE)
            else -> throw UploadException(ErrorResponse.UNKNOWN_RESPONSE)
        }
    }

    private fun reactToHttpErrorResponse(httpException: HttpException) {
        val errorResponse = retrofit.parseLinShareErrorResponse(httpException)
        throw UploadException(errorResponse)
    }
}
