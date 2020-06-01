package com.linagora.android.linshare.inject

import com.linagora.android.linshare.data.datasource.contact.ContactDataSource
import com.linagora.android.linshare.data.datasource.contact.DeviceContactDataSource
import com.linagora.android.linshare.data.repository.contact.ContactRepositoryImp
import com.linagora.android.linshare.domain.repository.contact.ContactRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ContactModule {

    @Binds
    @Singleton
    abstract fun bindContactRepository(contactRepositoryImp: ContactRepositoryImp): ContactRepository

    @Binds
    @Singleton
    abstract fun bindDeviceContactDataSource(contactDataSource: DeviceContactDataSource): ContactDataSource
}
