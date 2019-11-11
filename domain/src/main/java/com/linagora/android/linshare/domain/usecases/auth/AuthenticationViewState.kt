package com.linagora.android.linshare.domain.usecases.auth

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.ViewState

data class AuthenticationViewState(val credential: Credential, val token: Token) : Success.ViewState()
data class AuthenticationFailure(val exception: AuthenticationException) : Failure.FeatureFailure()
object SuccessRemoveAccount : ViewState()
