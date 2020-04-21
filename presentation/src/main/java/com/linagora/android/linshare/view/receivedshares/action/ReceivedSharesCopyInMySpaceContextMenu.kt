package com.linagora.android.linshare.view.receivedshares.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.receivedshare.ReceivedSharesCopyInMySpace
import com.linagora.android.linshare.view.action.CopyInMySpaceContextMenu
import com.linagora.android.linshare.view.base.BaseViewModel

class ReceivedSharesCopyInMySpaceContextMenu(
    private val viewModel: BaseViewModel
) : CopyInMySpaceContextMenu<Share> {

    override fun copyInMySpace(item: Share) {
        viewModel.dispatchUIState(Either.right(ReceivedSharesCopyInMySpace(item)))
    }
}
