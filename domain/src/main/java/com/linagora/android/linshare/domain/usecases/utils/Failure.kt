package com.linagora.android.linshare.domain.usecases.utils

import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    data class TimeoutError(val e: Exception) : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()

    object Error : Failure()
}
