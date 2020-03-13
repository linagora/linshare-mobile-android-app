package com.linagora.android.linshare.view.search

import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.adapter.myspace.MySpaceAdapter
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.search.NoResults
import com.linagora.android.linshare.domain.usecases.search.SearchInitial
import com.linagora.android.linshare.domain.usecases.search.SearchViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.view.base.ListItemBehavior

@BindingAdapter("searchState", "itemBehavior", requireAll = true)
fun bindingSearchResult(
    recyclerView: RecyclerView,
    searchState: Either<Failure, Success>?,
    itemBehavior: ListItemBehavior<Document>
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = MySpaceAdapter(itemBehavior)
    }

    println("bindingList $searchState")

    searchState?.fold(
        ifLeft = { recyclerView.isVisible = false },
        ifRight = { when (it) {
            is SearchInitial -> recyclerView.isVisible = false
            is SearchViewState -> {
                (recyclerView.adapter as MySpaceAdapter).submitList(it.documents)
                recyclerView.isVisible = true
            } }
        })
}

@BindingAdapter("visible")
fun bindingEmptyMessage(textView: TextView, searchState: Either<Failure, Success>?) {
    val visible = searchState
        ?.fold(ifLeft = { it is NoResults }, ifRight = { false })
    textView.isVisible = visible ?: false
}

@BindingAdapter("resultsCount")
fun bindingSearchResultCount(
    textView: TextView,
    searchState: Either<Failure, Success>?
) {
    val count = searchState?.fold(
        ifLeft = { 0 },
        ifRight = { when (it) {
            is SearchViewState -> it.documents.size
            else -> 0 } }
    )
    textView.text = textView.context.resources
        .getQuantityString(R.plurals.search_total_results, count ?: 0, count ?: 0)
}

@BindingAdapter("resultsCountContainer")
fun bindingSearchResultCountContainer(
    linearLayout: LinearLayout,
    searchState: Either<Failure, Success>?
) {
    val visible = searchState?.fold(
        ifLeft = { false },
        ifRight = { when (it) {
            is SearchViewState -> true
            else -> false } }
    )
    linearLayout.isVisible = visible ?: false
}
