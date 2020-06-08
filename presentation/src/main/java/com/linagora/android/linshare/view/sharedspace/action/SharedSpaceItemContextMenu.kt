package com.linagora.android.linshare.view.sharedspace.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.sharedspace.DetailsSharedSpaceItem
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ItemContextMenu

class SharedSpaceItemContextMenu(
    private val viewModel: BaseViewModel
) : ItemContextMenu<SharedSpaceNodeNested> {

    override fun details(data: SharedSpaceNodeNested) {
        viewModel.dispatchUIState(Either.right(DetailsSharedSpaceItem(data)))
    }

    override fun remove(data: SharedSpaceNodeNested) {
        TODO("Not yet implemented")
    }
}
