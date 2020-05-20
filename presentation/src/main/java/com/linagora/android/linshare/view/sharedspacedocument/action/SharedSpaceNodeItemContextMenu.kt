package com.linagora.android.linshare.view.sharedspacedocument.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeClick
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ItemContextMenu

class SharedSpaceNodeItemContextMenu(private val viewModel: BaseViewModel) : ItemContextMenu<WorkGroupNode> {

    override fun remove(data: WorkGroupNode) = viewModel.dispatchUIState(Either.right(RemoveSharedSpaceNodeClick(data)))
}
