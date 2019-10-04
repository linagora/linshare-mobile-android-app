package com.linagora.android.linshare.domain.usecases.utils

/**
 * Base Class for handling success/viewstates/navigation events
 * Every feature specific success should extend [FeatureSuccess] class.
 */
sealed class Success {

    /** * Extend this class for feature specific success.*/
    abstract class ViewState : Success()

    abstract class ViewEvent : Success()

    // App wide success view states
    object Idle : ViewState()

    object Loading : ViewState()

    // App wide success view events
    data class Message(val message: String) : ViewEvent()
}
