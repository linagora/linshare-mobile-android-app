package com.linagora.android.linshare.domain.network

import com.linagora.android.linshare.domain.usecases.utils.Failure

abstract class NetworkState : Failure.FeatureFailure()
object InternetNotAvailable : NetworkState()
