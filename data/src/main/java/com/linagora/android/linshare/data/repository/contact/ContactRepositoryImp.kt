package com.linagora.android.linshare.data.repository.contact

import com.linagora.android.linshare.data.datasource.contact.DeviceContactDataSource
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.contact.Contact
import com.linagora.android.linshare.domain.repository.contact.ContactRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImp @Inject constructor(
    private val contactDataSource: DeviceContactDataSource
) : ContactRepository {

    override suspend fun getContactsSuggestion(autoCompletePattern: AutoCompletePattern): List<Contact> {
        return contactDataSource.getContactsSuggestion(autoCompletePattern)
    }
}
