package com.linagora.android.linshare.adapter.myspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.DocumentDiffCallback
import com.linagora.android.linshare.databinding.MySpaceRowItemBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.view.base.ListItemBehavior

class MySpaceAdapter(
    private val itemBehavior: ListItemBehavior<Document>
) : ListAdapter<Document, MySpaceViewHolder>(DocumentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySpaceViewHolder {
        return MySpaceViewHolder(
            MySpaceRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            itemBehavior
        )
    }

    override fun onBindViewHolder(holder: MySpaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MySpaceViewHolder(
    private val binding: MySpaceRowItemBinding,
    private val itemBehavior: ListItemBehavior<Document>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(document: Document) {
        binding.document = document
        binding.itemBehavior = itemBehavior
        binding.executePendingBindings()
    }
}
