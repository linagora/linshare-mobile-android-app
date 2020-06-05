package com.linagora.android.linshare.adapter.sharedspacedestination

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import com.linagora.android.linshare.adapter.sharedspace.AdapterType
import com.linagora.android.linshare.adapter.sharedspace.SharedSpaceAdapter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.view.base.ListItemBehavior

@BindingAdapter("sharedSpaceDestinationState", "itemBehaviorDestination", requireAll = true)
fun bindingSharedSpaceDestinationList(
    recyclerView: RecyclerView,
    sharedSpaceState: Either<Failure, Success>?,
    itemBehavior: ListItemBehavior<SharedSpaceNodeNested>
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = SharedSpaceAdapter(
            itemBehavior,
            AdapterType.SHARE_SPACE_DESTINATION_PICKER
        )
    }

    sharedSpaceState?.fold(
        ifLeft = { recyclerView.isVisible = false },
        ifRight = {
            recyclerView.isVisible = true
            when (it) {
                is SharedSpaceViewState -> (recyclerView.adapter as SharedSpaceAdapter).submitList(it.sharedSpace)
                is SearchSharedSpaceViewState -> (recyclerView.adapter as SharedSpaceAdapter).submitList(it.sharedSpace)
            }
        })
}
