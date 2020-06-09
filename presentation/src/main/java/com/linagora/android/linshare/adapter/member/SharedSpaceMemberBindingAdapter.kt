package com.linagora.android.linshare.adapter.member

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetMembersSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.getAvatarCharacter
import com.linagora.android.linshare.util.toDisplayRoleNameId

@BindingAdapter("sharedSpaceMemberState")
fun bindingSharedSpaceMember(
    recyclerView: RecyclerView,
    sharedSpaceMemberState: Either<Failure, Success>
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = SharedSpaceMemberAdapter()
    }

    sharedSpaceMemberState.fold(
        ifLeft = { recyclerView.isVisible = false },
        ifRight = { success ->
            recyclerView.isVisible = true
            if (success is GetMembersSuccess) {
                (recyclerView.adapter as SharedSpaceMemberAdapter)
                    .submitList(success.members)
            }
        }
    )
}

@BindingAdapter("memberRole")
fun bindingMemberRole(textView: TextView, sharedSpaceMember: SharedSpaceMember) {
    val stringId = sharedSpaceMember.role.name.toDisplayRoleNameId()
    textView.text = textView.context.getString(stringId.value)
}

@BindingAdapter("memberAvatar")
fun bindingMemberAvatar(textView: TextView, sharedSpaceMember: SharedSpaceMember) {
    textView.text = sharedSpaceMember.getAvatarCharacter().toString()
}
