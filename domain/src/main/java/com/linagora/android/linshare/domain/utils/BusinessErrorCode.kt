package com.linagora.android.linshare.domain.utils

import com.linagora.android.linshare.domain.model.ClientErrorCode
import com.linagora.android.linshare.domain.model.LinShareErrorCode

object BusinessErrorCode {

    val QuotaAccountNoMoreSpaceErrorCode = LinShareErrorCode(46011)

    val InternetNotAvailableErrorCode = ClientErrorCode(1)

    val DeviceNotEnoughStorageErrorCode = ClientErrorCode(1100)

    val EmptyDocumentErrorCode = ClientErrorCode(1200)

    val WorkGroupNodeNotFoundErrorCode = LinShareErrorCode(26007)
}
