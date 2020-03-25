package com.linagora.android.linshare.domain.network

import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

abstract class NetworkState : Failure.FeatureFailure()
object InternetNotAvailable : NetworkState()
object InternetAvailable : Success.ViewState()
