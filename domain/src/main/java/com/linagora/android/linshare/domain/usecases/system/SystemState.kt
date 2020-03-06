package com.linagora.android.linshare.domain.usecases.system

import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure

class SystemState {
    object InternetNotAvailable : FeatureFailure()
}
