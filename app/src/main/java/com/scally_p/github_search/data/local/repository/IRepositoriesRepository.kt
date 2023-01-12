package com.scally_p.github_search.data.local.repository

import com.scally_p.github_search.model.Repositories


interface IRepositoriesRepository {
    suspend fun searchRepositories(query: String, page: Int): Result<Repositories>
}