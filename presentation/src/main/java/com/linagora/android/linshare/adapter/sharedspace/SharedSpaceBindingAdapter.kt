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
