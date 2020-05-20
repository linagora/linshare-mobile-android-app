package com.linagora.android.linshare.data.network

import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.UNKNOWN_RESPONSE
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import retrofit2.Retrofit

object RetrofitExtensions {
    val LOGGER: Logger = LoggerFactory.getLogger(RetrofitExtensions::class.java)
}

fun Retrofit.parseLinShareErrorResponse(httpException: HttpException): ErrorResponse {
    return runCatching {
        httpException.response()
            ?.errorBody()
            ?.let {
                val converter = responseBodyConverter<ErrorResponse>(
                    ErrorResponse::class.java,
                    arrayOfNulls<Annotation>(0))
                converter.convert(it)
            }
            ?: UNKNOWN_RESPONSE
    }.getOrElse {
        RetrofitExtensions.LOGGER.error("parseLinShareErrorResponse(): ${it.printStackTrace()}")
        UNKNOWN_RESPONSE
    }
}
