package com.linagora.android.linshare.view.share

import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter.StateSuggestionUser
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteNoResult
import com.linagora.android.linshare.domain.usecases.autocomplete.UserAutoCompleteViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.resources.LayoutId
import com.linagora.android.linshare.view.share.ShareFragment.Companion.AUTO_COMPLETE_THRESHOLD

@BindingAdapter("queryState")
fun bindingUserSuggestion(
    textView: AppCompatAutoCompleteTextView,
    queryState: Either<Failure, Success>?
) {

    if (textView.adapter == null) {
        textView.setAdapter(UserAutoCompleteAdapter(textView.context, LayoutId(R.layout.user_suggestion_item)))
    }

    queryState?.map { when (it) {
        is UserAutoCompleteViewState -> {
            (textView.adapter as UserAutoCompleteAdapter).submitList(it.results)
            submitStateSuggestions(textView, StateSuggestionUser.FOUND)
        }
        is AutoCompleteNoResult -> {
            submitStateSuggestions(textView, StateSuggestionUser.NOT_FOUND)
        }
    } }
}

private fun submitStateSuggestions(textView: AppCompatAutoCompleteTextView, state: StateSuggestionUser) {
    val adapter = textView.adapter as UserAutoCompleteAdapter
    adapter.submitStateSuggestions(state)
    showSuggestion(textView)
}

private fun showSuggestion(textView: AppCompatAutoCompleteTextView) {
    if (textView.length() >= AUTO_COMPLETE_THRESHOLD) {
        textView.showDropDown()
    }
}
