package com.linagora.android.linshare.view.myspace.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.myspace.RemoveClick
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ItemContextMenu

class MySpaceItemContextMenu(private val viewModel: BaseViewModel) : ItemContextMenu<Document> {

    override fun remove(data: Document) = viewModel.dispatchUIState(Either.right(RemoveClick(data)))
}
