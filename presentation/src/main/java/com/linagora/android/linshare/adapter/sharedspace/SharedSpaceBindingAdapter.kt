package com.linagora.android.linshare.adapter.sharedspace

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat

@BindingAdapter("sharedSpaceState", requireAll = true)
fun bindingSharedSpaceList(
    recyclerView: RecyclerView,
    sharedSpaceState: Either<Failure, Success>
) {
    if (recyclerView.adapter == null) { recyclerView.adapter = SharedSpaceAdapter() }

    sharedSpaceState.fold(
        ifLeft = { recyclerView.isVisible = false },
        ifRight = {
            when (it) {
                is SharedSpaceViewState -> {
                    recyclerView.isVisible = true
                    (recyclerView.adapter as SharedSpaceAdapter).submitList(it.sharedSpace)
                }
                else -> recyclerView.isVisible = false
            }
        })
}

@BindingAdapter("sharedSpaceLoadingState")
fun bindingSharedSpaceLoading(
    swipeRefreshLayout: SwipeRefreshLayout,
    sharedSpaceState: Either<Failure, Success>
) {
    sharedSpaceState.fold(
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
        textView.context.getString(
            R.string.last_modified,
            TimeUtils.convertToLocalTime(sharedSpaceNodeNested.modificationDate, LastModifiedFormat)
        )
    }.getOrNull()
}
