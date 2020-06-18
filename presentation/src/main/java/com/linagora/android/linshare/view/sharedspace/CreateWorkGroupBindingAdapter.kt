package com.linagora.android.linshare.view.sharedspace

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.sharedspace.BlankNameError
import com.linagora.android.linshare.domain.usecases.sharedspace.NameContainSpecialCharacter
import com.linagora.android.linshare.domain.usecases.sharedspace.ValidName
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.SuggestNewNameUtils
import com.linagora.android.linshare.util.SuggestNewNameUtils.SuggestNameType

@BindingAdapter("suggestNameWorkGroup")
fun bindingSuggestNameWorkGroup(
    editText: AppCompatEditText,
    listSharedSpaceNodeNested: List<SharedSpaceNodeNested>
) {
    val suggestName = SuggestNewNameUtils(editText.context).suggestNewName(
            listSharedSpaceNodeNested.map { it.name },
            SuggestNameType.WORKGROUP
    )
    editText.setText(suggestName)
    editText.setSelection(suggestName.length)
}

@BindingAdapter("errorMessageEnterNameWorkGroup")
fun bindingErrorMessageEnterNameWorkGroup(
    textView: AppCompatTextView,
    state: Either<Failure, Success>
) {
    state.fold(
        ifLeft = { failure ->
            textView.visibility = View.VISIBLE
            when (failure) {
                is BlankNameError -> textView.text = textView.context.getString(R.string.workgroup_name_already_exists)
                is NameContainSpecialCharacter -> textView.text = textView.context.getString(R.string.workgroup_name_not_contain_special_char)
            }
        },
        ifRight = { textView.visibility = View.GONE })
}

@BindingAdapter("enableCreateWorkGroupButton")
fun bindingEnableCreateWorkGroupButton(
    textView: TextView,
    state: Either<Failure, Success>
) {
    textView.isEnabled = state.fold(
        ifLeft = { false },
        ifRight = { success ->
            success.takeIf { it is ValidName }
                ?.let { true }
                ?: false
        }
    )
}
