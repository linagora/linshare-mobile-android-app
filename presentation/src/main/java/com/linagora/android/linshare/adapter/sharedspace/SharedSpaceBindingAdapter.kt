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

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.sharedspace.EmptySharedSpaceState
import com.linagora.android.linshare.domain.usecases.sharedspace.NoResultsSearchSharedSpace
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceFailure
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.linshare.view.base.ListItemBehavior

@BindingAdapter("sharedSpaceState", "itemBehavior", requireAll = true)
fun bindingSharedSpaceList(
    recyclerView: RecyclerView,
    sharedSpaceState: Either<Failure, Success>?,
    itemBehavior: ListItemBehavior<SharedSpaceNodeNested>
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = SharedSpaceAdapter(itemBehavior, AdapterType.NORMAL)
    }

    sharedSpaceState?.fold(
        ifLeft = { failure ->
            when (failure) {
                is SharedSpaceFailure, EmptySharedSpaceState -> false
                else -> recyclerView.isVisible = true
            }
        },
        ifRight = {
            recyclerView.isVisible = true
            when (it) {
                is SharedSpaceViewState -> (recyclerView.adapter as SharedSpaceAdapter).submitList(it.sharedSpace)
                is SearchSharedSpaceViewState -> (recyclerView.adapter as SharedSpaceAdapter).submitList(it.sharedSpace)
            }
        })
}

@BindingAdapter("sharedSpaceLoadingState")
fun bindingSharedSpaceLoading(
    swipeRefreshLayout: SwipeRefreshLayout,
    sharedSpaceState: Either<Failure, Success>?
) {
    sharedSpaceState?.fold(
        ifLeft = { swipeRefreshLayout.isRefreshing = false },
        ifRight = { success ->
            swipeRefreshLayout.isRefreshing = success.takeIf { success is Success.Loading }
                ?.let { true }
                ?: false
        }
    )
}

@BindingAdapter("sharedSpaceItemLastModified")
fun bindingSharedSpaceItemLastModified(
    textView: TextView,
    sharedSpaceNodeNested: SharedSpaceNodeNested
) {
    textView.text = runCatching {
        with(textView.context) {
            getString(R.string.last_modified, TimeUtils(this).convertToLocalTime(sharedSpaceNodeNested.modificationDate, LastModifiedFormat)) } }
        .getOrNull()
}

@BindingAdapter("resultsCountSharedSpace")
fun bindingSearchResultSharedSpaceCount(
    textView: TextView,
    searchState: Either<Failure, Success>?
) {
    searchState?.fold(
        ifLeft = { textView.setTextSearchResultSharedSpaceCount(0) },
        ifRight = {
            when (it) {
                is SearchSharedSpaceViewState -> textView.setTextSearchResultSharedSpaceCount(it.sharedSpace.size)
            }
        }
    )
}

private fun TextView.setTextSearchResultSharedSpaceCount(count: Int?) {
    text = context.resources
        .getQuantityString(R.plurals.search_total_results, count ?: 0, count ?: 0)
}

@BindingAdapter("resultsCountSharedSpaceContainer")
fun bindingSearchResultCountSharedSpaceContainer(
    linearLayout: LinearLayout,
    searchState: Either<Failure, Success>?
) {
    searchState?.fold(
        ifLeft = {
            when (it) {
                is NoResultsSearchSharedSpace -> linearLayout.isVisible = true
                is EmptySharedSpaceState -> linearLayout.isVisible = false
            }
        },
        ifRight = {
            when (it) {
                is SearchSharedSpaceViewState -> linearLayout.isVisible = true
                is SharedSpaceViewState -> linearLayout.isVisible = false
            }
        }
    )
}

@BindingAdapter("bindingTextEmptyMessage")
fun bindingTextEmptyMessage(textView: TextView, state: Either<Failure, Success>?) {
    state?.mapLeft { failure ->
        when (failure) {
            is NoResultsSearchSharedSpace -> textView.setText(R.string.search_no_results)
            else -> textView.setText(R.string.do_not_have_any_workgroup)
        }
    }
}

@BindingAdapter("visibilityMenuContainer")
fun bindingVisibilityMenuContainer(view: View, adapterType: AdapterType) {
    view.visibility = adapterType.takeIf { it == AdapterType.SHARE_SPACE_DESTINATION_PICKER }
        ?.let { View.GONE }
        ?: View.VISIBLE
}

@BindingAdapter("visibleEmptyMessageSharedSpace")
fun bindingEmptyMessageSharedSpace(textView: TextView, state: Either<Failure, Success>?) {
    val visible = state?.fold(
        ifLeft = { failure ->
            when (failure) {
                is SharedSpaceFailure, EmptySharedSpaceState -> true
                else -> false
            }
        },
        ifRight = { false })
    textView.isVisible = visible ?: false
}
