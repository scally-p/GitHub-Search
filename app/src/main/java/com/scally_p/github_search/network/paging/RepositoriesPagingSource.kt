package com.scally_p.github_search.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.scally_p.github_search.model.Repository
import com.scally_p.github_search.network.retrofit.RetrofitHelper
import com.scally_p.github_search.util.Constants
import retrofit2.HttpException
import java.io.IOException

class RepositoriesPagingSource(
    private val query: String
) : PagingSource<Int, Repository>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
        val page = params.key ?: Constants.Paging.PAGE_START
        val apiQuery = query + Constants.Api.IN_QUALIFIER
        return try {
            val response = RetrofitHelper.retrofitApiInstance.searchRepositories(apiQuery, page, params.loadSize)
            println("Check --- response: $response")
            val repos = response.body()?.items ?: ArrayList()
            val nextKey = if (repos.isNotEmpty()) {
                page + (params.loadSize / Constants.Paging.PAGE_SIZE)
            } else null
            LoadResult.Page(
                data = repos,
                prevKey = if (page == Constants.Paging.PAGE_START) null else page - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}