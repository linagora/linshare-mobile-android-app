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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.WorkGroupNodeDiffCallback
import com.linagora.android.linshare.databinding.SharedSpaceDocumentRowItemBinding
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeType
import com.linagora.android.linshare.util.Constant.ACTIVE_STATE_ALPHA
import com.linagora.android.linshare.util.Constant.INACTIVE_STATE_ALPHA
import com.linagora.android.linshare.view.base.ListItemBehavior

class SharedSpaceDocumentAdapter constructor(
    private val listItemBehavior: ListItemBehavior<WorkGroupNode>,
    private val adapterType: AdapterType
) : ListAdapter<WorkGroupNode, SharedSpaceDocumentViewHolder>(WorkGroupNodeDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedSpaceDocumentViewHolder {
        return SharedSpaceDocumentViewHolder(
            SharedSpaceDocumentRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listItemBehavior, adapterType
        )
    }

    override fun onBindViewHolder(holder: SharedSpaceDocumentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SharedSpaceDocumentViewHolder(
    private val binding: SharedSpaceDocumentRowItemBinding,
    private val listItemBehavior: ListItemBehavior<WorkGroupNode>,
    private val adapterType: AdapterType
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(workGroupNode: WorkGroupNode) {
        val nodeAlpha = adapterType.takeIf {
            it == AdapterType.SHARE_SPACE_DESTINATION_PICKER && workGroupNode.type !== WorkGroupNodeType.FOLDER }
            ?.let { INACTIVE_STATE_ALPHA } ?: ACTIVE_STATE_ALPHA

        binding.documentName.alpha = nodeAlpha
        binding.documentIcon.alpha = nodeAlpha

        binding.documentMenuContainer.visibility = adapterType.takeIf {
            it == AdapterType.SHARE_SPACE_DESTINATION_PICKER }
            ?.let { View.GONE }
            ?: View.VISIBLE
        binding.node = workGroupNode
        binding.listItemBehavior = listItemBehavior
        binding.executePendingBindings()
    }
}
