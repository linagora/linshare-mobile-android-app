package com.linagora.android.linshare.inject

import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.data.datasource.network.LinShareDocumentDataSource
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DocumentModule {

    @Binds
    @Singleton
    abstract fun bindLinshareDocumentDataSource(
        linShareDocumentDataSource: LinShareDocumentDataSource
    ): DocumentDataSource
}
