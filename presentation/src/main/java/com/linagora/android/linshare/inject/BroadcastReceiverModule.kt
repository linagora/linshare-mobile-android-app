package com.linagora.android.linshare.inject

import com.linagora.android.linshare.receiver.DownloadCompleteReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverModule {

    @ContributesAndroidInjector
    internal abstract fun downloadCompleteReceiver(): DownloadCompleteReceiver
}
