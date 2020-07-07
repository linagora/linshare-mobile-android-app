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
