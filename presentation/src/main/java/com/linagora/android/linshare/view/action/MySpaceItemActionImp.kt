package com.linagora.android.linshare.view.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.myspace.ShareItemClick
import com.linagora.android.linshare.view.base.BaseViewModel

class MySpaceItemActionImp(private val viewModel: BaseViewModel) : MySpaceItemAction<Document> {

    override fun onShareClick(data: Document) = viewModel.dispatchUIState(Either.right(ShareItemClick(data)))
}
