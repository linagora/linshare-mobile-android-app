package com.linagora.android.linshare.view.share

import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter.StateSuggestionUser
import com.linagora.android.linshare.domain.model.autocomplete.isEmailValid
import com.linagora.android.linshare.domain.model.autocomplete.toExternalUser
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteNoResult
import com.linagora.android.linshare.domain.usecases.autocomplete.AutoCompleteViewState
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
        textView.setAdapter(
            UserAutoCompleteAdapter(
                textView.context,
                LayoutId(R.layout.user_suggestion_item)
            )
        )
    }

    queryState?.map { success ->
        val adapter = textView.adapter as UserAutoCompleteAdapter
        when (success) {
            is AutoCompleteViewState -> {
                adapter.submitList(success.results)
                submitStateSuggestions(textView, StateSuggestionUser.FOUND)
            }
            is AutoCompleteNoResult -> {
                success.pattern.takeIf { it.isEmailValid() }
                    ?.let {
                        adapter.submitList(listOf(it.toExternalUser()))
                        submitStateSuggestions(textView, StateSuggestionUser.EXTERNAL_USER)
                    } ?: submitStateSuggestions(textView, StateSuggestionUser.NOT_FOUND)
            }
        }
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
