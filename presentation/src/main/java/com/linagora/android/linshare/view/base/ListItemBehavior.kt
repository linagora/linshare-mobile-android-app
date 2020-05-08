package com.linagora.android.linshare.view.base

interface ListItemBehavior<T> {

    fun onContextMenuClick(data: T)

    fun onItemClick(data: T) {}
}
