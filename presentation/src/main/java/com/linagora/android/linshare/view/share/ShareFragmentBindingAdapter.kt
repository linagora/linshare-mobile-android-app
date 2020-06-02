package com.linagora.android.linshare.view.share

import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter.StateSuggestionUser
import com.linagora.android.linshare.domain.model.autocomplete.isEmailValid
import com.linagora.android.linshare.domain.model.autocomplete.toExternalUser
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteViewState
import com.linagora.android.linshare.domain.usecases.autocomplete.ReceiverSuggestionNoResult
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.resources.LayoutId
import com.linagora.android.linshare.util.binding.AddRecipientsViewBindingExtension.AUTO_COMPLETE_THRESHOLD

@BindingAdapter("queryState")
fun bindingUserSuggestion(
    textView: AppCompatAutoCompleteTextView,
    queryState: Either<Failure, Success>?
) {

    if (textView.adapter == null) {
        textView.setAdapter(UserAutoCompleteAdapter(
            textView.context,
            LayoutId(R.layout.user_suggestion_item)))
    }

    queryState?.fold(
        ifLeft = { reactOnFailureQuerySuggestion(textView, it) },
        ifRight = { reactOnSuccessQuerySuggestion(textView, it) })
}

private fun reactOnFailureQuerySuggestion(autoCompleteView: AppCompatAutoCompleteTextView, failure: Failure) {
    val adapter = autoCompleteView.adapter as UserAutoCompleteAdapter
    if (failure is ReceiverSuggestionNoResult) {
        failure.pattern.takeIf { it.isEmailValid() }
            ?.let {
                adapter.submitList(listOf(it.toExternalUser()))
                submitStateSuggestions(autoCompleteView, StateSuggestionUser.EXTERNAL_USER)
            } ?: submitStateSuggestions(autoCompleteView, StateSuggestionUser.NOT_FOUND)
    }
}

private fun reactOnSuccessQuerySuggestion(autoCompleteView: AppCompatAutoCompleteTextView, success: Success) {
    val adapter = autoCompleteView.adapter as UserAutoCompleteAdapter
    if (success is AutoCompleteViewState) {
        adapter.submitList(success.results)
        submitStateSuggestions(autoCompleteView, StateSuggestionUser.FOUND)
    }
}

private fun submitStateSuggestions(
    textView: AppCompatAutoCompleteTextView,
    state: StateSuggestionUser
) {
    val adapter = textView.adapter as UserAutoCompleteAdapter
    adapter.submitStateSuggestions(state)
    showSuggestion(textView)
}

private fun showSuggestion(textView: AppCompatAutoCompleteTextView) {
    if (textView.length() >= AUTO_COMPLETE_THRESHOLD) {
        textView.showDropDown()
    }
}
