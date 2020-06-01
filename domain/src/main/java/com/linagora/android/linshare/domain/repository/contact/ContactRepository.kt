package com.linagora.android.linshare.domain.repository.contact

import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.contact.Contact

interface ContactRepository {
    suspend fun getContactsSuggestion(autoCompletePattern: AutoCompletePattern): List<Contact>
}
