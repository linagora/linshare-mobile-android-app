package com.linagora.android.linshare.adapter.sharedspace

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllSharedSpaceRolesFailed
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllSharedSpaceRolesSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
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
