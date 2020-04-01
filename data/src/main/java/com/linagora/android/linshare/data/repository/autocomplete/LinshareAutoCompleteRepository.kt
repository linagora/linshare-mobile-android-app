package com.linagora.android.linshare.data.repository.autocomplete

import com.linagora.android.linshare.data.datasource.autocomplete.AutoCompleteDataSource
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.repository.autocomplete.AutoCompleteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinshareAutoCompleteRepository @Inject constructor(
    private val autoCompleteDataSource: AutoCompleteDataSource
) : AutoCompleteRepository {

    override suspend fun getAutoComplete(
        autoCompletePattern: AutoCompletePattern,
        autoCompleteType: AutoCompleteType
    ): List<AutoCompleteResult> {
        return autoCompleteDataSource.getAutoComplete(autoCompletePattern, autoCompleteType)
    }
}
