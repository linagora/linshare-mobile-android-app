package com.linagora.android.linshare.domain.usecases.autocomplete

import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.contact.Contact
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Success.ViewState

data class AutoCompleteViewState(val results: List<AutoCompleteResult>) : ViewState()
data class UserAutoCompleteViewState(val results: List<UserAutoCompleteResult>) : ViewState()
data class AutoCompleteNoResult(val pattern: AutoCompletePattern) : FeatureFailure()
data class AutoCompleteFailure(val throwable: Throwable) : FeatureFailure()
data class ContactSuggestionFailure(val throwable: Throwable) : FeatureFailure()
data class ContactSuggestionSuccess(val suggestions: List<Contact>) : ViewState()
data class ReceiverSuggestionNoResult(val pattern: AutoCompletePattern) : FeatureFailure()
object CombineReceiverSuggestion : ViewState()
