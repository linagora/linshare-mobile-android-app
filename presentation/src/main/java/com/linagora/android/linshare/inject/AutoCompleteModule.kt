package com.linagora.android.linshare.inject

import com.linagora.android.linshare.data.datasource.autocomplete.AutoCompleteDataSource
import com.linagora.android.linshare.data.datasource.autocomplete.LinshareAutoCompleteDataSource
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AutoCompleteModule {

    @Binds
    @Singleton
    abstract fun bindAutoCompleteDataSource(
        linshareAutoCompleteDataSource: LinshareAutoCompleteDataSource
    ): AutoCompleteDataSource
}
