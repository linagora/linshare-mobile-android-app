/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.adapter.sharedspace

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import arrow.core.Either
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.canUpload
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceDocumentNoResult
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceDocumentViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentEmpty
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentFailure
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.linshare.util.getDrawableIcon
import com.linagora.android.linshare.view.base.ListItemBehavior

@BindingAdapter("sharedSpaceDocumentState", "listItemBehavior", "adapterType", requireAll = true)
fun bindingSharedSpaceDocumentList(
    recyclerView: RecyclerView,
    sharedSpaceDocumentState: Either<Failure, Success>,
    listItemBehavior: ListItemBehavior<WorkGroupNode>,
    adapterType: AdapterType
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = SharedSpaceDocumentAdapter(listItemBehavior, adapterType)
    }

    sharedSpaceDocumentState.fold(
        ifLeft = { failure -> when (failure) {
            is SharedSpaceDocumentFailure, SearchSharedSpaceDocumentNoResult -> recyclerView.isVisible = false
        } },
        ifRight = { success -> submitSharedSpaceDocumentList(recyclerView, success, adapterType) }
    )
}

private fun submitSharedSpaceDocumentList(recyclerView: RecyclerView, success: Success, adapterType: AdapterType) {
    recyclerView.isVisible = true
    val documents = when (success) {
        is SharedSpaceDocumentViewState -> {
            adapterType.takeIf { it == AdapterType.SHARE_SPACE_DESTINATION_PICKER }
                ?.let { success.documents.filterIsInstance<WorkGroupFolder>() }
                ?: success.documents
        }
        is SearchSharedSpaceDocumentViewState -> success.documents
        is SharedSpaceDocumentEmpty -> {
            recyclerView.isVisible = false
            emptyList()
        }
        else -> null
    }

    documents?.let { submitListSharedSpaceDocument(documents, recyclerView) }
}

private fun submitListSharedSpaceDocument(documents: List<WorkGroupNode>, recyclerView: RecyclerView) {
    if (recyclerView.adapter is SharedSpaceDocumentAdapter) {
        (recyclerView.adapter as SharedSpaceDocumentAdapter).submitList(documents)
    }
}

@BindingAdapter("sharedSpaceDocumentState")
fun bindingSharedSpaceDocumentLoading(
    swipeRefreshLayout: SwipeRefreshLayout,
    sharedSpaceDocumentState: Either<Failure, Success>
) {
    sharedSpaceDocumentState.fold(
        ifLeft = { swipeRefreshLayout.isRefreshing = false },
        ifRight = { success ->
            swipeRefreshLayout.isRefreshing = success.takeIf { it is Success.Loading }
                ?.let { true }
                ?: false
        }
    )
}

@BindingAdapter("sharedSpaceDocumentLastModified")
fun bindingSharedSpaceDocumentLastModified(
    textView: TextView,
    workGroupNode: WorkGroupNode
) {
    textView.text = runCatching { with(textView.context) {
            getString(R.string.last_modified, TimeUtils(this).convertToLocalTime(workGroupNode.modificationDate, LastModifiedFormat)) } }
        .getOrNull()
}

@BindingAdapter("sharedSpaceDocumentIcon")
fun bindingSharedSpaceDocumentIcon(imageView: ImageView, workGroupNode: WorkGroupNode) {
    val drawableICon = when (workGroupNode) {
        is WorkGroupDocument -> workGroupNode.mimeType.getDrawableIcon()
        else -> R.drawable.ic_folder
    }
    GlideApp.with(imageView.context)
        .load(drawableICon)
        .into(imageView)
}

@BindingAdapter("sharedSpaceDocumentAddButton", "isSearchingState")
fun bindingSharedSpaceDocumentAddButton(
    floatingActionButton: FloatingActionButton,
    currentSharedSpace: SharedSpace?,
    isSearchingState: Boolean
) {
    val enable = currentSharedSpace
        ?.takeIf { sharedSpace -> sharedSpace.role.canUpload() }
        ?.let { true }
        ?: false

    val backgroundColor = enable.takeIf { enable }
        ?.let { R.color.colorPrimary }
        ?: R.color.disable_state_color

    val visible = currentSharedSpace
        ?.let { !isSearchingState }
        ?: false

    floatingActionButton.isEnabled = enable
    floatingActionButton.isVisible = visible
    floatingActionButton.backgroundTintList = ColorStateList
        .valueOf(ContextCompat.getColor(floatingActionButton.context, backgroundColor))
}

@BindingAdapter("emptyMessageInSharedSpaceDocument")
fun bindingTextEmptyMessageInSharedSpaceDocument(textView: TextView, state: Either<Failure, Success>?) {
    state?.fold(
        ifLeft = { failure -> failure.takeIf { it is SearchSharedSpaceDocumentNoResult }
            ?.let { textView.setText(R.string.search_no_results) }
            ?: textView.setText(R.string.do_not_have_any_document) },
        ifRight = { textView.setText(R.string.do_not_have_any_document) }
    )
}

@BindingAdapter("contextActionVisible", "operationRoles", requireAll = true)
fun bindingContextActionWithRole(
    linearLayout: LinearLayout,
    sharedSpaceRole: SharedSpaceRole,
    operationRoles: List<SharedSpaceRoleName>
) {
    val visible = operationRoles.takeIf { it.isNotEmpty() && it.contains(sharedSpaceRole.name) }
        ?.let { View.VISIBLE }
        ?: View.GONE
    linearLayout.visibility = visible
}
