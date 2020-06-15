package com.linagora.android.linshare.data.datasource.autocomplete

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinshareAutoCompleteDataSource @Inject constructor(
    private val linshareApi: LinshareApi
) : AutoCompleteDataSource {

    override suspend fun getAutoComplete(
        autoCompletePattern: AutoCompletePattern,
        autoCompleteType: AutoCompleteType,
        threadUUID: SharedSpaceId?
    ): List<AutoCompleteResult> {
        return when (autoCompleteType) {
            AutoCompleteType.SHARING -> linshareApi.getSharingAutoComplete(autoCompletePattern.value)
            AutoCompleteType.THREAD_MEMBERS -> getThreadMemberAutoComplete(autoCompletePattern, threadUUID)
        }
    }

    private suspend fun getThreadMemberAutoComplete(
        autoCompletePattern: AutoCompletePattern,
        threadUUID: SharedSpaceId?
    ): List<AutoCompleteResult> {
        require(threadUUID != null) { "Provide threadUUID to get ThreadMemberAutoComplete " }
        return linshareApi.getAutoCompleteThreadMembers(autoCompletePattern.value, threadUUID!!.uuid.toString())
    }
}
