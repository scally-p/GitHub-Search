package com.scally_p.github_search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scally_p.github_search.databinding.ItemRepositoryBinding
import com.scally_p.github_search.model.Repository
import com.scally_p.github_search.util.ImageUtils

class RepositoryViewHolder(private val binding: ItemRepositoryBinding) :
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
    }

    companion object {
        fun create(parent: ViewGroup): RepositoryViewHolder {
            val binding =
                ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RepositoryViewHolder(binding)
        }
    }
}
