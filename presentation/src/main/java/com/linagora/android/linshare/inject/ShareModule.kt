package com.linagora.android.linshare.inject

import com.linagora.android.linshare.data.datasource.ReceivedShareDataSource
import com.linagora.android.linshare.data.datasource.network.LinShareReceivedShareDataSource
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ShareModule {

    @Binds
    @Singleton
    abstract fun bindLinshareDocumentDataSource(
        linShareReceivedShareDataSource: LinShareReceivedShareDataSource
    ): ReceivedShareDataSource
}
