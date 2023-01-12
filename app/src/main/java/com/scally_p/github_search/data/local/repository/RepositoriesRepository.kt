package com.scally_p.github_search.data.local.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.scally_p.github_search.model.Repository
import com.scally_p.github_search.network.paging.RepositoriesPagingSource
import com.scally_p.github_search.util.Constants
import kotlinx.coroutines.flow.Flow

class RepositoriesRepository : IRepositoriesRepository {

    private val tag: String = RepositoriesRepository::class.java.name

    override fun getSearchResultStream(query: String): Flow<PagingData<Repository>> {
        println("Check --- here 12")
        return Pager(
            config = PagingConfig(
                pageSize = Constants.Paging.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RepositoriesPagingSource(query) }
        ).flow
    }
}