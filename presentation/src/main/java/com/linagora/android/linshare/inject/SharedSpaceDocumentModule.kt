package com.linagora.android.linshare.inject

import com.linagora.android.linshare.data.datasource.network.LinShareSharedSpacesDocumentDataSource
import com.linagora.android.linshare.data.datasource.sharedspacesdocument.SharedSpacesDocumentDataSource
import com.linagora.android.linshare.data.repository.sharedspace.SharedSpacesDocumentRepositoryImp
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class SharedSpaceDocumentModule {
    @Binds
    @Singleton
    abstract fun bindSharedSpaceDocumentDataSource(
        linShareSharedSpacesDocumentDataSource: LinShareSharedSpacesDocumentDataSource
    ): SharedSpacesDocumentDataSource

    @Binds
    @Singleton
    abstract fun bindSharedSpaceDocumentRepository(
        sharedSpacesDocumentRepository: SharedSpacesDocumentRepositoryImp
    ): SharedSpacesDocumentRepository
}
