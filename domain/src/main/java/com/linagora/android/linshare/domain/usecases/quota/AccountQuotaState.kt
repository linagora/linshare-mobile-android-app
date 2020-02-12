package com.linagora.android.linshare.domain.usecases.quota

import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Failure.QuotaAccountError
import com.linagora.android.linshare.domain.usecases.utils.Success.ViewState
import com.linagora.android.linshare.domain.utils.BusinessErrorCode

object ValidAccountQuota : ViewState()
object ExceedMaxFileSize : FeatureFailure()
object QuotaAccountNoMoreSpaceAvailable :
    QuotaAccountError(BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode)
