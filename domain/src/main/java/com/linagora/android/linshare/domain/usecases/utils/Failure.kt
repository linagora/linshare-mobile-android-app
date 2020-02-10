package com.linagora.android.linshare.domain.usecases.utils

import com.linagora.android.linshare.domain.model.ErrorCode
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.utils.BusinessErrorCode

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    data class TimeoutError(val e: Exception) : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()

    object Error : Failure()

    abstract class QuotaError : Failure()

    abstract class QuotaAccountError(errorCode: ErrorCode) : QuotaError()

    object QuotaAccountNoMoreSpaceAvailable :
        QuotaAccountError(BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode)
}
