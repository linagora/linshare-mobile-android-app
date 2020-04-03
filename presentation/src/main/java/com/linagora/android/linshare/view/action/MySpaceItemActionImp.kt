package com.linagora.android.linshare.view.action

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.myspace.ShareItemClick
import com.linagora.android.linshare.view.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MySpaceItemActionImp(private val viewModel: BaseViewModel) : MySpaceItemAction<Document> {

    override fun onShareClick(data: Document) {
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            viewModel.dispatchState(Either.right(ShareItemClick(data)))
        }
    }
}
