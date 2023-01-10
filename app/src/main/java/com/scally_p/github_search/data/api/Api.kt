package com.scally_p.github_search.data.api

import com.scally_p.github_search.model.Repositories
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("search/repositories")
    suspend fun getRepositories(@Query("q") q: String): Response<Repositories>

}
