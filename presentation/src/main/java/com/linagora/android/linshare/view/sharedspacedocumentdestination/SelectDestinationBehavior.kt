package com.linagora.android.linshare.view.sharedspacedocumentdestination

import arrow.core.Either
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.OnSelectedDestination

class SelectDestinationBehavior(val viewModel: BaseViewModel) : OnSelectedDestination {
    override fun onChoose() {
        viewModel.dispatchUIState(Either.right(ChoosePickDestinationViewState))
    }

    override fun onCancel() {
        viewModel.dispatchUIState(Either.right(CancelPickDestinationViewState))
    }
}
