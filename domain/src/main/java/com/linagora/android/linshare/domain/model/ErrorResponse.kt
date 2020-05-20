package com.linagora.android.linshare.domain.model

import com.linagora.android.linshare.domain.utils.BusinessErrorCode.DeviceNotEnoughStorageErrorCode
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.EmptyDocumentErrorCode
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.InternetNotAvailableErrorCode
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.WorkGroupNodeNotFoundErrorCode

data class ErrorResponse(val message: String, val errCode: BaseErrorCode?) {
    companion object {
        private val UNKNOWN_LINSHARE_ERROR_CODE = null

        const val DEVICE_NOT_ENOUGH_SPACE_MESSAGE = "write failed: ENOSPC (No space left on device)"

        val UNKNOWN_RESPONSE = ErrorResponse("unknown error", UNKNOWN_LINSHARE_ERROR_CODE)

        val FILE_NOT_FOUND = ErrorResponse("file_not_found", UNKNOWN_LINSHARE_ERROR_CODE)

        val INTERNET_NOT_AVAILABLE = ErrorResponse("internet_not_available", InternetNotAvailableErrorCode)

        val DEVICE_NOT_ENOUGH_STORAGE = ErrorResponse(DEVICE_NOT_ENOUGH_SPACE_MESSAGE, DeviceNotEnoughStorageErrorCode)

        val EMPTY_DOCUMENT_ERROR_RESPONSE = ErrorResponse("empty document", EmptyDocumentErrorCode)

        val REMOVE_NODE_NOT_FOUND_ERROR_RESPONSE = ErrorResponse("WorkGroupNode not found", WorkGroupNodeNotFoundErrorCode)
    }
}
