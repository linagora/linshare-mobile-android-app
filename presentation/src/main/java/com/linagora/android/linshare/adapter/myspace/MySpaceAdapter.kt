package com.linagora.android.linshare.adapter.myspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.myspace.diff.DocumentDiffCallback
import com.linagora.android.linshare.databinding.MySpaceRowItemBinding
import com.linagora.android.linshare.domain.model.document.Document

class MySpaceAdapter : ListAdapter<Document, MySpaceViewHolder>(DocumentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySpaceViewHolder {
        println("onCreateViewHolder")
        return MySpaceViewHolder(
            MySpaceRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: MySpaceViewHolder, position: Int) {
        println("onBindViewHolder $position")
        holder.bind(getItem(position))
    }
}

class MySpaceViewHolder(
    private val binding: MySpaceRowItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(document: Document) {
        println("bind $document")
        binding.document = document
        binding.executePendingBindings()
    }
}
