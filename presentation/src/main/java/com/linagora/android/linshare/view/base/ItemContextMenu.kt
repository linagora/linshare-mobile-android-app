package com.linagora.android.linshare.view.base

interface ItemContextMenu<T> {

    fun onDownloadClick(data: T)

    fun onRemoveClick(data: T)
}
