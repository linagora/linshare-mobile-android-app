package com.linagora.android.linshare.view.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arrow.core.Either
import com.linagora.android.linshare.domain.usecases.search.CloseSearchView
import com.linagora.android.linshare.domain.usecases.search.OpenSearchView
import com.linagora.android.linshare.view.base.BaseViewModel

class SearchActionImp constructor(private val baseViewModel: BaseViewModel) : SearchAction {
    private val mutableIsSearch = MutableLiveData(false)

    override val isSearching: LiveData<Boolean>
        get() = mutableIsSearch

    override fun openSearchView() {
        mutableIsSearch.value = true
        baseViewModel.dispatchUIState(Either.right(OpenSearchView))
    }

    override fun closeSearchView() {
        mutableIsSearch.value = false
        baseViewModel.dispatchUIState(Either.right(CloseSearchView))
    }
}
