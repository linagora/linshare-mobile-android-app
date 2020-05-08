package com.linagora.android.linshare.view.sharedspace.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceItemClick
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ListItemBehavior

class SharedSpaceItemBehavior(private val viewModel: BaseViewModel) :
    ListItemBehavior<SharedSpaceNodeNested> {
    override fun onContextMenuClick(data: SharedSpaceNodeNested) {
        viewModel.dispatchUIState(Either.right(SharedSpaceContextMenuClick(data)))
    }

    override fun onItemClick(data: SharedSpaceNodeNested) {
        viewModel.dispatchUIState(Either.right(SharedSpaceItemClick(data)))
    }
}
