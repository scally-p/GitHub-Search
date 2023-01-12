package com.scally_p.github_search.data.local.repository

import androidx.paging.PagingData
import com.scally_p.github_search.model.Repository
import kotlinx.coroutines.flow.Flow

interface IRepositoriesRepository {
    fun getSearchResultStream(query: String): Flow<PagingData<Repository>>
}