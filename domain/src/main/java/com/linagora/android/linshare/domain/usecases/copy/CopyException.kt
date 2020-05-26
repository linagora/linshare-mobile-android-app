package com.linagora.android.linshare.domain.usecases.copy

import com.linagora.android.linshare.domain.model.ErrorResponse

class CopyException(val errorResponse: ErrorResponse) : RuntimeException()
