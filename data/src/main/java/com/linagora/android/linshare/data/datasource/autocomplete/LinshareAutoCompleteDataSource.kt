package com.linagora.android.linshare.data.datasource.autocomplete

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinshareAutoCompleteDataSource @Inject constructor(
    private val linshareApi: LinshareApi
) : AutoCompleteDataSource {

    override suspend fun getAutoComplete(
        autoCompletePattern: AutoCompletePattern,
        autoCompleteType: AutoCompleteType
    ): List<AutoCompleteResult> {
        return when (autoCompleteType) {
            AutoCompleteType.SHARING -> linshareApi.getSharingAutoComplete(autoCompletePattern.value)
        }
    }
}
