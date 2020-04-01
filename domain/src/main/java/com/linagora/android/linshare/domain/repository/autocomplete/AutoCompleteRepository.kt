package com.linagora.android.linshare.domain.repository.autocomplete

import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType

interface AutoCompleteRepository {

    suspend fun getAutoComplete(
        autoCompletePattern: AutoCompletePattern,
        autoCompleteType: AutoCompleteType
    ): List<AutoCompleteResult>
}
