package com.linagora.android.linshare.domain.usecases.autocomplete

import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Success.ViewState

data class AutoCompleteViewState(val results: List<AutoCompleteResult>) : ViewState()
data class UserAutoCompleteViewState(val results: List<UserAutoCompleteResult>) : ViewState()
data class AutoCompleteNoResult(val pattern: AutoCompletePattern) : ViewState()
data class AutoCompleteFailure(val throwable: Throwable) : FeatureFailure()
