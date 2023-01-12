package com.scally_p.github_search.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.scally_p.github_search.databinding.ItemRepositoryBinding
import com.scally_p.github_search.databinding.ItemRepositoryFooterBinding
import com.scally_p.github_search.model.Repository
import com.scally_p.github_search.util.ImageUtils

class RepositoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val tag: String = RepositoryAdapter::class.java.name

    var repositories = ArrayList<Repository>()
    var footerLoading: Boolean = false

    fun setList(value: ArrayList<Repository>) {
        repositories.remove(Repository(footer = true))
        repositories.addAll(value)
        notifyItemRangeChanged(0, repositories.size)
        footerLoading = false
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        repositories.clear()
        notifyDataSetChanged()
    }

    fun addFooterLoader() {
        repositories.add(Repository(footer = true))
        footerLoading = true
    }

    inner class FooterLoaderItemViewHolder(private val binding: ItemRepositoryFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repository: Repository) {
        }
    }

    inner class ItemViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repository: Repository) {
            ImageUtils.setGlideImage(
                binding.root,
                binding.avatar,
                repository.owner?.avatarUrl ?: ""
            )

            binding.ownerName.text = repository.owner?.login ?: ""
            binding.repositoryTitle.text = repository.name ?: ""
            binding.repositoryName.text = repository.fullName ?: ""
            binding.repositoryDescription.text = repository.description ?: ""
            binding.repositoryUrl.text = repository.url ?: ""

            binding.repositoryDescription.isVisible = repository.description?.isNotEmpty() == true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (repositories[position].footer) {
            TYPE_FOOTER_LOADER
        } else {
            TYPE_REPOSITORY_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == TYPE_FOOTER_LOADER) {
            val binding =
                ItemRepositoryFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            FooterLoaderItemViewHolder(binding)
        } else {
            val binding =
                ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return repositories.size
    }

    private fun getItem(position: Int): Repository {
        return repositories[position]
    }

    companion object {
        const val TYPE_FOOTER_LOADER = 1
        const val TYPE_REPOSITORY_ITEM = 2
    }
}