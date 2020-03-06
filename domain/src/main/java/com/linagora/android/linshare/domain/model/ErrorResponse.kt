package com.linagora.android.linshare.domain.model

import com.linagora.android.linshare.domain.utils.BusinessErrorCode.InternetNotAvailableErrorCode

data class ErrorResponse(val message: String, val errCode: BaseErrorCode?) {
    companion object {
        private val UNKNOWN_LINSHARE_ERROR_CODE = null

        val UNKNOWN_RESPONSE = ErrorResponse("unknown error", UNKNOWN_LINSHARE_ERROR_CODE)

        val FILE_NOT_FOUND = ErrorResponse("file_not_found", UNKNOWN_LINSHARE_ERROR_CODE)

        val INTERNET_NOT_AVAILABLE = ErrorResponse("internet_not_available", InternetNotAvailableErrorCode)
    }
}
