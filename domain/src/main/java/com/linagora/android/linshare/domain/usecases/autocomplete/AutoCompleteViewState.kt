package com.linagora.android.linshare.domain.usecases.autocomplete

import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Success.ViewState

data class AutoCompleteViewState(val results: List<AutoCompleteNoResult>) : ViewState()
data class UserAutoCompleteViewState(val results: List<UserAutoCompleteResult>) : ViewState()
object AutoCompleteNoResult : ViewState()
data class AutoCompleteFailure(val throwable: Throwable) : FeatureFailure()
