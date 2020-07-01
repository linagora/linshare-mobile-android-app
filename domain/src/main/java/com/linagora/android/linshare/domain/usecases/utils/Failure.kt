package com.linagora.android.linshare.domain.usecases.utils

import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    data class TimeoutError(val e: Exception) : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()

    abstract class ViewEventFailure : Failure()

    object Error : Failure()

    abstract class QuotaError : Failure()

    abstract class QuotaAccountError(linShareErrorCode: LinShareErrorCode) : QuotaError()

    class CannotExecuteWithoutNetwork(val operatorType: OperatorType) : ViewEventFailure()
}

typealias onFailure = (Failure) -> Unit

object NoOpOnFailure : onFailure {
    override fun invoke(failure: Failure) {
        // do nothing
    }
}
