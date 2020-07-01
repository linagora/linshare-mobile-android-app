package com.linagora.android.linshare.view.base

import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.LinShareApplication

abstract class LinShareViewModel(
    override val internetAvailable: ConnectionLiveData,
    val application: LinShareApplication,
    dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(internetAvailable, dispatcherProvider)
