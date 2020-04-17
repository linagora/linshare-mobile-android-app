package com.linagora.android.linshare.domain.usecases.upload

import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.model.ErrorResponse.Companion.DEVICE_NOT_ENOUGH_STORAGE
import com.linagora.android.linshare.domain.model.ErrorResponse.Companion.EMPTY_DOCUMENT_ERROR_RESPONSE

open class UploadException(val errorResponse: ErrorResponse) : RuntimeException()
object NotEnoughDeviceStorageException : UploadException(DEVICE_NOT_ENOUGH_STORAGE)
object EmptyDocumentException : UploadException(EMPTY_DOCUMENT_ERROR_RESPONSE)
