package com.scally_p.github_search.ui.data

import com.scally_p.github_search.model.Repository

sealed class UiModel {
    data class RepositoryItem(val repository: Repository) : UiModel()
    data class SeparatorItem(val string: String) : UiModel()
}