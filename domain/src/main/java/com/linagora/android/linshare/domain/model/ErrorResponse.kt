package com.linagora.android.linshare.domain.model

data class ErrorResponse(val message: String, val errorCode: ErrorCode) {
    companion object {
        val UNKNOWN_RESPONSE =
            ErrorResponse("empty message", ErrorCode(0))
    }
}
