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

package com.linagora.android.linshare.adapter.myspace

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.functionality.Functionality
import com.linagora.android.linshare.domain.model.functionality.FunctionalityIdentifier
import com.linagora.android.linshare.domain.usecases.myspace.EmptyMySpaceState
import com.linagora.android.linshare.domain.usecases.myspace.MySpaceFailure
import com.linagora.android.linshare.domain.usecases.myspace.MySpaceViewState
import com.linagora.android.linshare.domain.usecases.myspace.SearchDocumentNoResult
import com.linagora.android.linshare.domain.usecases.myspace.SearchDocumentViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.linshare.util.getDrawableIcon
import com.linagora.android.linshare.view.base.ListItemBehavior

@BindingAdapter("mySpaceState", "itemBehavior", requireAll = true)
fun bindingMySpaceList(
    recyclerView: RecyclerView,
    mySpaceState: Either<Failure, Success>,
    itemBehavior: ListItemBehavior<Document>
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = MySpaceAdapter(itemBehavior)
    }

    mySpaceState.fold(
        ifLeft = { when (it) {
            is MySpaceFailure, EmptyMySpaceState, SearchDocumentNoResult -> recyclerView.isVisible = false }
        },
        ifRight = { when (it) {
            is MySpaceViewState -> {
                recyclerView.isVisible = true
                (recyclerView.adapter as MySpaceAdapter).submitList(it.documents)
            }
            is SearchDocumentViewState -> {
                recyclerView.isVisible = true
                (recyclerView.adapter as MySpaceAdapter).submitList(it.documents)
            } }
        })
}

@BindingAdapter("mySpaceState")
fun bindingMySpaceLoading(
    swipeRefreshLayout: SwipeRefreshLayout,
    mySpaceState: Either<Failure, Success>
) {

    mySpaceState.fold(
        ifLeft = { swipeRefreshLayout.isRefreshing = false },
        ifRight = {
            when (it) {
                is Success.Idle -> swipeRefreshLayout.isRefreshing = false
                is Success.Loading -> swipeRefreshLayout.isRefreshing = true
                is MySpaceViewState, is SearchDocumentViewState -> swipeRefreshLayout.isRefreshing = false
            }
        }
    )
}

@BindingAdapter("mySpaceItemName")
fun bindingMySpaceItemName(
    textView: TextView,
    document: Document
) {
    textView.text = document.name
}

@BindingAdapter("mySpaceItemLastModified")
fun bindingMySpaceItemLastModified(
    textView: TextView,
    document: Document
) {
    textView.text = runCatching { with(textView.context) {
            getString(R.string.last_modified, TimeUtils(this)
                .convertToLocalTime(document.modificationDate, LastModifiedFormat)) } }
        .getOrNull()
}

@BindingAdapter("documentMediaType")
fun bindingDocumentIcon(imageView: ImageView, document: Document) {
    GlideApp.with(imageView.context)
        .load(document.type.getDrawableIcon())
        .placeholder(R.drawable.ic_file)
        .into(imageView)
}

@BindingAdapter("visibleEmptyMessageMySpace")
fun bindingEmptyMessageMySpace(linearLayout: LinearLayout, state: Either<Failure, Success>) {
    state.fold(
        ifLeft = { failure ->
            when (failure) {
                is MySpaceFailure, EmptyMySpaceState -> linearLayout.isVisible = true
            }
        },
        ifRight = { success ->
            when (success) {
                is MySpaceViewState -> linearLayout.isVisible = false
            }
        })
}

@BindingAdapter("workGroupContextActionVisibleWithFunctionality")
fun bindingWorkGroupContextActionWithFunctionality(
    view: View,
    allFunctionalities: List<Functionality>
) {
    view.isVisible = allFunctionalities.takeIf { it.isNotEmpty() }
        ?.first { functionality -> functionality.identifier == FunctionalityIdentifier.WORK_GROUP }
        ?.enable
        ?: false
}
