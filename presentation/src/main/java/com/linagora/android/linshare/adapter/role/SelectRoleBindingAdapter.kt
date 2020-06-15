package com.linagora.android.linshare.adapter.role

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.util.toDisplayRoleNameId

@BindingAdapter("roleName", "lastSelectedRole", requireAll = true)
fun bindingSelectRoleName(textView: TextView, sharedSpaceRole: SharedSpaceRole, lastSelectRole: SharedSpaceRole) {
    textView.text = textView.context.getString(sharedSpaceRole.name.toDisplayRoleNameId().value)
    val textColorId = sharedSpaceRole.takeIf { it == lastSelectRole }
        ?.let { R.color.colorPrimary }
        ?: R.color.file_name_color
    textView.setTextColor(ContextCompat.getColor(textView.context, textColorId))
}

@BindingAdapter("roleName", "lastSelectedRole", requireAll = true)
fun bindingSelectedRole(imageView: ImageView, sharedSpaceRole: SharedSpaceRole, lastSelectRole: SharedSpaceRole) {
    sharedSpaceRole.takeIf { it == lastSelectRole }
        ?.let { imageView.visibility = View.VISIBLE }
        ?: run { imageView.visibility = View.GONE }
}
