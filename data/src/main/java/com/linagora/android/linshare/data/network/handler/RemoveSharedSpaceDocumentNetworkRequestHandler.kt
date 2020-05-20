package com.linagora.android.linshare.data.network.handler

import com.linagora.android.linshare.data.network.parseLinShareErrorResponse
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveNotFoundSharedSpaceDocumentException
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceDocumentException
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.UNKNOWN_RESPONSE
import com.linagora.android.linshare.domain.utils.OnCatch
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveSharedSpaceDocumentNetworkRequestHandler @Inject constructor(
    private val retrofit: Retrofit
) : OnCatch {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RemoveSharedSpaceDocumentNetworkRequestHandler::class.java)
    }

    override fun invoke(throwable: Throwable) {
        LOGGER.error("invoke(): ${throwable.message} - ${throwable.printStackTrace()}")
        when (throwable) {
            is HttpException -> reactToHttpErrorResponse(throwable)
            else -> throw RemoveSharedSpaceDocumentException(UNKNOWN_RESPONSE)
        }
    }

    private fun reactToHttpErrorResponse(httpException: HttpException) {
        val errorResponse = retrofit.parseLinShareErrorResponse(httpException)
        errorResponse.errCode.takeIf { it == BusinessErrorCode.WorkGroupNodeNotFoundErrorCode }
            ?.let { throw RemoveNotFoundSharedSpaceDocumentException }
            ?: throw RemoveSharedSpaceDocumentException(errorResponse)
    }
}
