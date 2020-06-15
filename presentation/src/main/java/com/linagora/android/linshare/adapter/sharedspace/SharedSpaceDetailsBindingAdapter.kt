package com.linagora.android.linshare.adapter.sharedspace

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

@BindingAdapter("sharedSpaceDetailsTitle")
fun bindingDetailsTitle(textView: TextView, sharedSpaceDetailsState: Either<Failure, Success>) {
    sharedSpaceDetailsState.map { success ->
        if (success is GetSharedSpaceSuccess) {
            textView.text = success.sharedSpace.name
        }
    }
}

@BindingAdapter("contextActionVisible", "operationRoles", requireAll = true)
fun bindingAddMembersWithRole(
    addMembersButton: FloatingActionButton,
    sharedSpaceDetailsState: Either<Failure, Success>,
    operationRoles: List<SharedSpaceRoleName>
) {
    sharedSpaceDetailsState.map { success ->
        if (success is GetSharedSpaceSuccess) {
            val visible = operationRoles.takeIf { it.isNotEmpty() && it.contains(success.sharedSpace.role.name) }
                ?.let { View.VISIBLE }
                ?: View.GONE
            addMembersButton.visibility = visible
        }
    }
}
