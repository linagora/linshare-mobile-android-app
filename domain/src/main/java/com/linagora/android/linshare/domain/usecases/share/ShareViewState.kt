package com.linagora.android.linshare.domain.usecases.share

import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class ShareViewState(val shares: List<Share>) : Success.ViewState()
data class ShareFailureState(val throwable: Throwable) : Failure.FeatureFailure()
data class AddRecipient(val user: GenericUser) : Success.ViewEvent()
data class AddMailingList(val mailingList: MailingList) : Success.ViewEvent()
object ShareButtonClick : Success.ViewEvent()
