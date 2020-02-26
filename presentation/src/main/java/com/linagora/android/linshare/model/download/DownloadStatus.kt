package com.linagora.android.linshare.model.download

sealed class DownloadStatus {
    object DownloadFailed : DownloadStatus()
    object DownloadSuccess : DownloadStatus()
}
