package com.linagora.android.linshare.adapter.sharedspace.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.usecases.sharedspace.DownloadSharedSpaceNodeClick
import com.linagora.android.linshare.view.action.DownloadItemContextMenu
import com.linagora.android.linshare.view.base.BaseViewModel
import java.util.concurrent.atomic.AtomicReference

class SharedSpaceNodeDownloadContextMenu(
    private val viewModel: BaseViewModel
) : DownloadItemContextMenu<WorkGroupNode> {

    override val downloadingData: AtomicReference<WorkGroupNode>
        get() = downloadingShare

    private val downloadingShare = AtomicReference<WorkGroupNode>()

    override fun download(data: WorkGroupNode) {
        setDownloading(data)
        viewModel.dispatchState(Either.right(DownloadSharedSpaceNodeClick(data)))
    }
}
