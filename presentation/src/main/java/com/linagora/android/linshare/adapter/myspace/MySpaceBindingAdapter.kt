package com.linagora.android.linshare.adapter.myspace

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.myspace.MySpaceViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.linshare.util.getDrawableIcon
import com.linagora.android.linshare.view.myspace.MySpaceViewModel

@BindingAdapter("mySpaceState", "viewModel", requireAll = true)
fun bindingMySpaceList(
    recyclerView: RecyclerView,
    mySpaceState: Either<Failure, Success>,
    viewModel: MySpaceViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = MySpaceAdapter(viewModel)
    }

    mySpaceState.fold(
        ifLeft = {
            recyclerView.isVisible = false
        },
        ifRight = {
            when (it) {
                is MySpaceViewState -> {
                    recyclerView.isVisible = true
                    (recyclerView.adapter as MySpaceAdapter).submitList(it.documents)
                }
            }
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
                is Success.Loading -> swipeRefreshLayout.isRefreshing = true
                is MySpaceViewState -> swipeRefreshLayout.isRefreshing = false
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
    textView.text = runCatching { TimeUtils.convertToLocalTime(document.modificationDate, LastModifiedFormat) }
        .getOrNull()
}

@BindingAdapter("documentMediaType")
fun bindingDocumentIcon(imageView: ImageView, document: Document) {
    GlideApp.with(imageView.context)
        .load(document.type.getDrawableIcon())
        .placeholder(R.drawable.ic_file)
        .into(imageView)
}
