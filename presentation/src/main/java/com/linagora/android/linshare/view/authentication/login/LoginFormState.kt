package com.linagora.android.linshare.view.authentication.login

import com.linagora.android.linshare.domain.usecases.utils.Success.ViewEvent
import com.linagora.android.linshare.view.authentication.login.ErrorType.NONE

data class LoginFormState(
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val errorType: ErrorType = NONE
) : ViewEvent() {
    companion object {
        val INIT_STATE = LoginFormState()
    }
}

enum class ErrorType {
    WRONG_URL,
    WRONG_CREDENTIAL,
    UNKNOWN_ERROR,
    NONE
}
