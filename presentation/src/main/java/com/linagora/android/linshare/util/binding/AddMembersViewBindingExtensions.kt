package com.linagora.android.linshare.util.binding

import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
import com.linagora.android.linshare.databinding.AddMembersViewBinding
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllSharedSpaceRolesSuccess
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.OnAddMember
import com.linagora.android.linshare.util.OnRequestMemberAutoComplete
import com.linagora.android.linshare.util.binding.AddRecipientsViewBindingExtension.AUTO_COMPLETE_THRESHOLD
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.showKeyboard

fun AddMembersViewBinding.initView() {
    addMembers.apply {
        threshold = AUTO_COMPLETE_THRESHOLD
    }
}

fun AddMembersViewBinding.bindingRoles(success: Success) {
    if (success is GetAllSharedSpaceRolesSuccess) {
        sharedSpaceRoles = success.roles
    }
}

fun AddMembersViewBinding.bindingDefaultSelectedRole(success: Success) {
    if (selectedRole == null && success is GetAllSharedSpaceRolesSuccess) {
        selectedRole = success.roles.firstOrNull { role -> role.name == SharedSpaceRoleName.READER }
    }
}

fun AddMembersViewBinding.queryAfterTextChange(onRequestMember: OnRequestMemberAutoComplete) {
    addMembers.doAfterTextChanged { pattern ->
        pattern?.toString()
            ?.takeIf { it.isNotBlank() && it.length >= AUTO_COMPLETE_THRESHOLD }
            ?.let { AutoCompletePattern(it) }
            ?.let { onRequestMember(it, AutoCompleteType.THREAD_MEMBERS, sharedSpaceId!!) }
    }
}

fun AddMembersViewBinding.onSelectedMember(onAddMember: OnAddMember) {
    addMembers.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
        addMembers.text.clear()
        val selectedUser = parent.getItemAtPosition(position) as AutoCompleteResult
        onAddMember(sharedSpaceId!!, selectedUser, selectedRole!!)
    }
}

fun AddMembersViewBinding.showKeyBoard(viewState: Success.ViewState) {
    if (viewState is GetAllSharedSpaceRolesSuccess) {
        with(addMembers) {
            requestFocus()
            findFocus().showKeyboard()
        }
    }
}

fun AddMembersViewBinding.clearFocus() {
    with(addMembers) {
        dismissKeyboard()
        clearFocus()
    }
}
