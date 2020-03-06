package com.linagora.android.linshare.domain.utils

import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.model.SystemErrorCode

object BusinessErrorCode {

    val QuotaAccountNoMoreSpaceErrorCode = LinShareErrorCode(46011)

    val InternetNotAvailableErrorCode = SystemErrorCode(46012)
}
