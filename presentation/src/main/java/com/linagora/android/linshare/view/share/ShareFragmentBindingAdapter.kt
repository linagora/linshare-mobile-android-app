package com.linagora.android.linshare.view.share

import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter.StateSuggestionUser
import com.linagora.android.linshare.domain.model.autocomplete.isEmailValid
import com.linagora.android.linshare.domain.model.autocomplete.toExternalUser
import com.linagora.android.linshare.domain.usecases.autocomplete.ReceiverSuggestionNoResult
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.resources.LayoutId
import com.linagora.android.linshare.util.reactOnSuccessQuerySuggestion
import com.linagora.android.linshare.util.submitStateSuggestions

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
        ifRight = { textView.reactOnSuccessQuerySuggestion(it) })
}

private fun reactOnFailureQuerySuggestion(autoCompleteView: AppCompatAutoCompleteTextView, failure: Failure) {
    val adapter = autoCompleteView.adapter as UserAutoCompleteAdapter
    if (failure is ReceiverSuggestionNoResult) {
        failure.pattern.takeIf { it.isEmailValid() }
            ?.let {
                adapter.submitList(listOf(it.toExternalUser()))
                autoCompleteView.submitStateSuggestions(StateSuggestionUser.EXTERNAL_USER)
            } ?: autoCompleteView.submitStateSuggestions(StateSuggestionUser.NOT_FOUND)
    }
}
