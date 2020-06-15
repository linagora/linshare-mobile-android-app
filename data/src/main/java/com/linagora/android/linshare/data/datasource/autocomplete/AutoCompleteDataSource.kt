package com.linagora.android.linshare.data.datasource.autocomplete

import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId

interface AutoCompleteDataSource {

    suspend fun getAutoComplete(
        autoCompletePattern: AutoCompletePattern,
        autoCompleteType: AutoCompleteType,
        threadUUID: SharedSpaceId? = null
    ): List<AutoCompleteResult>
}
