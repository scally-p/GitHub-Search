package com.scally_p.github_search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.scally_p.github_search.databinding.ItemRepositoryFooterBinding

class RepositoryLoadStateViewHolder(
    private val binding: ItemRepositoryFooterBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retry.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.error.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retry.isVisible = loadState is LoadState.Error
        binding.error.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): RepositoryLoadStateViewHolder {
            val binding =
                ItemRepositoryFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return RepositoryLoadStateViewHolder(binding, retry)
        }
    }
}