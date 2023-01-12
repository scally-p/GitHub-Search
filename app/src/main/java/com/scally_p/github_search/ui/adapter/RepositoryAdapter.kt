package com.scally_p.github_search.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scally_p.github_search.ui.data.UiModel

class RepositoryAdapter : PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(UI_MODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RepositoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.RepositoryItem -> (holder as RepositoryViewHolder).bind(uiModel.repository)
                else -> {}
            }
        }
    }

    companion object {
        private val UI_MODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.RepositoryItem && newItem is UiModel.RepositoryItem &&
                        oldItem.repository.fullName == newItem.repository.fullName) ||
                        (oldItem is UiModel.RepositoryItem && newItem is UiModel.RepositoryItem &&
                                oldItem.repository.description == newItem.repository.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }
}
