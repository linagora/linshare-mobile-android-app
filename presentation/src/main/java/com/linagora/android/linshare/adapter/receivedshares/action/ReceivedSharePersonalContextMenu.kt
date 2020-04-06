package com.linagora.android.linshare.adapter.receivedshares.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.receivedshare.DownloadReceivedShareClick
import com.linagora.android.linshare.view.action.PersonalItemContextMenu
import com.linagora.android.linshare.view.base.BaseViewModel
import java.util.concurrent.atomic.AtomicReference

class ReceivedSharePersonalContextMenu(
    private val viewModel: BaseViewModel
) : PersonalItemContextMenu<Share> {

    override val downloadingData: AtomicReference<Share>
        get() = downloadingShare

    private val downloadingShare = AtomicReference<Share>()

    override fun download(data: Share) {
        setDownloading(data)
        viewModel.dispatchState(Either.right(DownloadReceivedShareClick(data)))
    }
}
