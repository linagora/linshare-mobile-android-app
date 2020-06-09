package com.linagora.android.linshare.adapter.sharedspace

import android.widget.TextView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

@BindingAdapter("sharedSpaceDetailsTitle")
fun bindingDetailsTitle(textView: TextView, sharedSpaceDetailsState: Either<Failure, Success>) {
    sharedSpaceDetailsState.map { success ->
        if (success is GetSharedSpaceSuccess) {
            textView.text = success.sharedSpace.name
        }
    }
}
