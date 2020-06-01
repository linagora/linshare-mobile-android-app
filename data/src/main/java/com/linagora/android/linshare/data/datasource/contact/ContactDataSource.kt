package com.linagora.android.linshare.data.datasource.contact

import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.contact.Contact

interface ContactDataSource {
    suspend fun getContactsSuggestion(autoCompletePattern: AutoCompletePattern): List<Contact>
}
