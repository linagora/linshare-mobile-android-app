package com.linagora.android.linshare.view.receivedshares

import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.base.BaseViewModel
import javax.inject.Inject

class ReceivedSharesViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider)
