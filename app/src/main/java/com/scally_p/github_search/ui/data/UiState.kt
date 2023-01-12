package com.scally_p.github_search.ui.data

import androidx.paging.PagingData
import com.scally_p.github_search.util.Constants

data class UiState(
    val query: String = Constants.Api.DEFAULT_QUERY,
    val lastQueryScrolled: String = Constants.Api.DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
    val pagingData: PagingData<UiModel> = PagingData.empty()
)