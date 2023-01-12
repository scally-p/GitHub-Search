package com.scally_p.github_search.data.local.repository

import com.scally_p.github_search.extension.exceptionOrNull
import com.scally_p.github_search.model.Repositories
import com.scally_p.github_search.network.NetworkHelper
import com.scally_p.github_search.network.RetrofitHelper
import com.scally_p.github_search.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoriesRepository : IRepositoriesRepository {

    private val tag: String = RepositoriesRepository::class.java.name

    override suspend fun searchRepositories(query: String, page: Int): Result<Repositories> {
        val apiResponse = NetworkHelper.apiRequest {
            RetrofitHelper.retrofitApiInstance.searchRepositories(
                query,
                page,
                Constants.Paging.PAGE_SIZE
            )
        }

        return withContext(Dispatchers.IO) {
            val body = apiResponse.getOrNull()?.body()
            if (apiResponse.isSuccess && body != null) {
                Result.success(body)
            } else {
                Result.failure(apiResponse.exceptionOrNull() ?: Exception("Request Failed"))
            }
        }
    }
}