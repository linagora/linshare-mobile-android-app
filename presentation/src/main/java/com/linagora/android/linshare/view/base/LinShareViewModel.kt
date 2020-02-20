package com.linagora.android.linshare.view.base

import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.LinShareApplication

abstract class LinShareViewModel(
    val application: LinShareApplication,
    dispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel(dispatcherProvider)
