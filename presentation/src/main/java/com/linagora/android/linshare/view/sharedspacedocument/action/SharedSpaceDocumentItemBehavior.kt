package com.linagora.android.linshare.view.sharedspacedocument.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceFolderContextMenuClick
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.ListItemBehavior

class SharedSpaceDocumentItemBehavior constructor(val viewModel: BaseViewModel) :
    ListItemBehavior<WorkGroupNode> {
    override fun onContextMenuClick(data: WorkGroupNode) {
        viewModel.dispatchUIState(Either.right(data.takeIf { it is WorkGroupDocument }
            ?.let { SharedSpaceDocumentContextMenuClick(data as WorkGroupDocument) }
            ?: SharedSpaceFolderContextMenuClick(data as WorkGroupFolder)))
    }

    override fun onItemClick(data: WorkGroupNode) {
        viewModel.dispatchUIState(Either.right(SharedSpaceDocumentItemClick(data)))
    }
}
