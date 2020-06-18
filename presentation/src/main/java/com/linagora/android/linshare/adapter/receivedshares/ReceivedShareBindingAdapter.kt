package com.linagora.android.linshare.adapter.receivedshares

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.receivedshare.ReceivedSharesViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.linshare.util.getDrawableIcon
import com.linagora.android.linshare.view.base.ListItemBehavior

@BindingAdapter("receivedListState", "itemBehavior", requireAll = true)
fun bindingReceivedList(recyclerView: RecyclerView, receivedListState: Either<Failure, Success>, itemBehavior: ListItemBehavior<Share>) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = ReceivedAdapter(itemBehavior)
    }

    receivedListState.fold(
        ifLeft = { recyclerView.isVisible = false },
        ifRight = {
            when (it) {
                is ReceivedSharesViewState -> {
                    recyclerView.isVisible = true
                    (recyclerView.adapter as ReceivedAdapter).submitList(it.receivedList)
                }
            }
        })
}

@BindingAdapter("receivedListLoadingState")
fun bindingReceivedListLoading(swipeRefreshLayout: SwipeRefreshLayout, receivedListState: Either<Failure, Success>) {
    receivedListState.fold(
        ifLeft = { swipeRefreshLayout.isRefreshing = false },
        ifRight = {
            when (it) {
                is Success.Loading -> swipeRefreshLayout.isRefreshing = true
                is ReceivedSharesViewState -> swipeRefreshLayout.isRefreshing = false
            }
        }
    )
}

@BindingAdapter("receivedCreationDate")
fun bindingReceivedLastModified(textView: TextView, share: Share) {
    textView.text = runCatching { with(textView.context) {
            getString(R.string.created, TimeUtils(this).convertToLocalTime(share.creationDate, LastModifiedFormat)) } }
        .getOrNull()
}

@BindingAdapter("receivedMediaType")
fun bindingReceivedIcon(imageView: ImageView, share: Share) {
    GlideApp.with(imageView.context)
        .load(share.type.getDrawableIcon())
        .placeholder(R.drawable.ic_file)
        .into(imageView)
}
