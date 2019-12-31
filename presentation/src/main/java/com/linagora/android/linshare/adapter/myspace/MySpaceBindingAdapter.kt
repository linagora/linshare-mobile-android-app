package com.linagora.android.linshare.adapter.myspace

import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.myspace.MySpaceViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat

@BindingAdapter("mySpaceState")
fun bindingMySpaceList(
    recyclerView: RecyclerView,
    mySpaceState: Either<Failure, Success>
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = MySpaceAdapter()
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
                else -> recyclerView.isVisible = false
            }
        })
}

@BindingAdapter("mySpaceState")
fun bindingMySpaceLoading(
    progressBar: ProgressBar,
    mySpaceState: Either<Failure, Success>
) {

    mySpaceState.fold(
        ifLeft = { progressBar.isVisible = false },
        ifRight = {
            when (it) {
                is Loading -> {
                    progressBar.isVisible = true
                }
                else -> progressBar.isVisible = false
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
    textView.text = runCatching {
        TimeUtils.convertToLocalTime(document.modificationDate, LastModifiedFormat)
    }.getOrNull()
}
