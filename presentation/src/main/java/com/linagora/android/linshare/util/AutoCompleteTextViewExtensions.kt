package com.linagora.android.linshare.util

import android.widget.AutoCompleteTextView
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter.StateSuggestionUser
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteViewState
import com.linagora.android.linshare.domain.usecases.autocomplete.ThreadMembersAutoCompleteViewState
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.binding.AddRecipientsViewBindingExtension

fun AutoCompleteTextView.submitStateSuggestions(stateSuggestion: StateSuggestionUser) {
    if (adapter is UserAutoCompleteAdapter) {
        val adapter = adapter as UserAutoCompleteAdapter
        adapter.submitStateSuggestions(stateSuggestion)
        showSuggestion()
    }
}

private fun AutoCompleteTextView.showSuggestion() {
    if (length() >= AddRecipientsViewBindingExtension.AUTO_COMPLETE_THRESHOLD) {
        showDropDown()
    }
}

fun AutoCompleteTextView.reactOnSuccessQuerySuggestion(success: Success) {
    if (adapter is UserAutoCompleteAdapter) {
        val adapter = adapter as UserAutoCompleteAdapter
        when (success) {
            is AutoCompleteViewState -> {
                adapter.submitList(success.results)
                submitStateSuggestions((StateSuggestionUser.FOUND))
            }
            is ThreadMembersAutoCompleteViewState -> {
                adapter.submitList(success.results)
                submitStateSuggestions((StateSuggestionUser.FOUND))
            }
        }
    }
}
