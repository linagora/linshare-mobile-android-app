package com.linagora.android.linshare.view.action

import arrow.core.Either
import com.linagora.android.linshare.domain.usecases.search.CloseSearchView
import com.linagora.android.linshare.domain.usecases.search.OpenSearchView
import com.linagora.android.linshare.view.base.BaseViewModel

class SearchActionImp constructor(private val baseViewModel: BaseViewModel) : SearchAction {

    override fun openSearchView() {
        baseViewModel.dispatchUIState(Either.right(OpenSearchView))
    }

    override fun closeSearchView() {
        baseViewModel.dispatchUIState(Either.right(CloseSearchView))
    }
}
