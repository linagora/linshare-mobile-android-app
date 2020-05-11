package com.linagora.android.linshare.view.sharedspacedocument

import arrow.core.Either
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnBackClick
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.widget.NavigationPathBehavior

class SharedSpaceNavigationPathBehavior(val viewModel: BaseViewModel) : NavigationPathBehavior {
    override fun onBack() {
        viewModel.dispatchUIState(Either.right(SharedSpaceDocumentOnBackClick))
    }
}
