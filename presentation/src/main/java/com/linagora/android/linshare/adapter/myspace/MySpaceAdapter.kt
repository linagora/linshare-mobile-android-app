package com.linagora.android.linshare.adapter.myspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.myspace.diff.DocumentDiffCallback
import com.linagora.android.linshare.databinding.MySpaceRowItemBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.view.myspace.MySpaceViewModel

class MySpaceAdapter(
    private val mySpaceViewModel: MySpaceViewModel
) : ListAdapter<Document, MySpaceViewHolder>(DocumentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySpaceViewHolder {
        return MySpaceViewHolder(
            MySpaceRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            mySpaceViewModel
        )
    }

    override fun onBindViewHolder(holder: MySpaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MySpaceViewHolder(
    private val binding: MySpaceRowItemBinding,
    private val mySpaceViewModel: MySpaceViewModel
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(document: Document) {
        binding.document = document
        binding.viewModel = mySpaceViewModel
        binding.executePendingBindings()
    }
}
