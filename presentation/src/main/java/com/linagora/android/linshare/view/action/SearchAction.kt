package com.linagora.android.linshare.view.action

import androidx.lifecycle.LiveData

interface SearchAction {

    val isSearching: LiveData<Boolean>

    fun openSearchView()

    fun closeSearchView()
}
