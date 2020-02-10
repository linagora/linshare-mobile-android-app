package com.linagora.android.linshare.domain.usecases.upload

import com.linagora.android.linshare.domain.model.ErrorResponse

class UploadException(errorResponse: ErrorResponse) : RuntimeException()
