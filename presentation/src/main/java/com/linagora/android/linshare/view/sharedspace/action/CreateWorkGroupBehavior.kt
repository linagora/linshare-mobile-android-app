package com.linagora.android.linshare.view.sharedspace.action

import arrow.core.Either
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateWorkGroupViewState
import com.linagora.android.linshare.view.base.BaseViewModel
import com.linagora.android.linshare.view.base.OnSelectedCreateBehavior

class CreateWorkGroupBehavior(val viewModel: BaseViewModel) : OnSelectedCreateBehavior {
    override fun onCreate(newNameRequest: NewNameRequest) {
        viewModel.dispatchUIState(Either.right(CreateWorkGroupViewState(newNameRequest)))
    }
}
