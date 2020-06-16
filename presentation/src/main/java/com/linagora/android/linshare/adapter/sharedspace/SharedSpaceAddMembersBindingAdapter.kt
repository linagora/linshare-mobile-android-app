package com.linagora.android.linshare.adapter.sharedspace

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter.StateSuggestionUser
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetMembersSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllSharedSpaceRolesFailed
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllSharedSpaceRolesSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.resources.LayoutId
import com.linagora.android.linshare.util.reactOnSuccessQuerySuggestion
import com.linagora.android.linshare.util.submitStateSuggestions
import com.linagora.android.linshare.util.toDisplayRoleNameId

@BindingAdapter("visibleAddMember")
fun bindingVisibleAddMember(
    addMemberContainer: ConstraintLayout,
    sharedSpaceRolesState: Either<Failure, Success>?
) {
    sharedSpaceRolesState?.fold(
        ifLeft = {
            if (it is GetAllSharedSpaceRolesFailed) {
                addMemberContainer.visibility = View.GONE
            }
        },
        ifRight = {
            if (it is GetAllSharedSpaceRolesSuccess) {
                addMemberContainer.visibility = View.VISIBLE
            }
        }
    )
}

@BindingAdapter("selectedRole")
fun bindingSelectedRole(textView: TextView, sharedSpaceRole: SharedSpaceRole?) {
    sharedSpaceRole
        ?.apply {
            textView.visibility = View.VISIBLE
            textView.text = textView.context.getString(name.toDisplayRoleNameId().value)
        }
        ?: run { textView.visibility = View.GONE }
}

@BindingAdapter("memberAutoCompleteQueryState")
fun bindingMemberSuggestion(
    textView: AutoCompleteTextView,
    queryState: Either<Failure, Success>?
) {
    if (textView.adapter == null) {
        textView.setAdapter(UserAutoCompleteAdapter(
            textView.context,
            LayoutId(R.layout.user_suggestion_item)
        ))
    }

    queryState?.fold(
        ifLeft = { textView.submitStateSuggestions(StateSuggestionUser.NOT_FOUND) },
        ifRight = { textView.reactOnSuccessQuerySuggestion(it) }
    )
}

@BindingAdapter("countMembers")
fun bindingMemberCount(textView: TextView, sharedSpaceMemberState: Either<Failure, Success>) {
    sharedSpaceMemberState.fold(
        ifLeft = { textView.isVisible = false },
        ifRight = { success ->
            textView.isVisible = true
            if (success is GetMembersSuccess) {
                val totalMembers = success.members.size
                textView.text = textView.context.resources
                    .getQuantityString(R.plurals.existing_member, totalMembers, totalMembers)
            }
        }
    )
}
