package com.linagora.android.linshare.view.action

import java.util.concurrent.atomic.AtomicReference

interface DownloadItemContextMenu<T> {

    val downloadingData: AtomicReference<T>

    fun download(data: T)

    fun setDownloading(data: T?) {
        downloadingData.set(data)
    }
}
