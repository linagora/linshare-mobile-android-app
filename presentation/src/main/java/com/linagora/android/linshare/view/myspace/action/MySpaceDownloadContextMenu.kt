package com.linagora.android.linshare.view.myspace.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.myspace.DownloadClick
import com.linagora.android.linshare.view.action.DownloadItemContextMenu
import com.linagora.android.linshare.view.base.BaseViewModel
import java.util.concurrent.atomic.AtomicReference

class MySpaceDownloadContextMenu(private val viewModel: BaseViewModel) : DownloadItemContextMenu<Document> {

    override val downloadingData: AtomicReference<Document>
        get() = downloadingDocument

    private val downloadingDocument = AtomicReference<Document>()

    override fun download(data: Document) {
        setDownloading(data)
        viewModel.dispatchUIState(Either.right(DownloadClick(data)))
    }
}
