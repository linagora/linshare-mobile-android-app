package com.linagora.android.linshare.adapter.sharedspace.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.usecases.sharedspace.DownloadSharedSpaceDocumentClick
import com.linagora.android.linshare.view.action.DownloadItemContextMenu
import com.linagora.android.linshare.view.base.BaseViewModel
import java.util.concurrent.atomic.AtomicReference

class SharedSpaceDocumentDownloadContextMenu(
    private val viewModel: BaseViewModel
) : DownloadItemContextMenu<WorkGroupDocument> {

    override val downloadingData: AtomicReference<WorkGroupDocument>
        get() = downloadingShare

    private val downloadingShare = AtomicReference<WorkGroupDocument>()

    override fun download(data: WorkGroupDocument) {
        setDownloading(data)
        viewModel.dispatchState(Either.right(DownloadSharedSpaceDocumentClick(data)))
    }
}
