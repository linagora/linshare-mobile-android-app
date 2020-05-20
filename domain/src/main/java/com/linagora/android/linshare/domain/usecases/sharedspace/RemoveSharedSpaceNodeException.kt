package com.linagora.android.linshare.domain.usecases.sharedspace

import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.REMOVE_NODE_NOT_FOUND_ERROR_RESPONSE

class RemoveSharedSpaceNodeException(throwable: Throwable) : RuntimeException()
open class RemoveSharedSpaceDocumentException(val errorResponse: ErrorResponse) : RuntimeException()
object RemoveNotFoundSharedSpaceDocumentException : RemoveSharedSpaceDocumentException(REMOVE_NODE_NOT_FOUND_ERROR_RESPONSE)
