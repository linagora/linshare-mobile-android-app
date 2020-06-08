package com.linagora.android.linshare.view.base

interface ItemContextMenu<T> {

    fun details(data: T)

    fun remove(data: T)
}
