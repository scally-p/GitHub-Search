package com.scally_p.github_search.data.network

import com.google.gson.GsonBuilder
import com.scally_p.github_search.BuildConfig
import com.scally_p.github_search.data.api.Api
import com.scally_p.github_search.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper  {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                })
        .build()

    private val gsonConverterFactory by lazy {
        GsonConverterFactory.create(
            GsonBuilder()
                .serializeNulls()
                .create()
        )
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.Urls.SERVER)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    val retrofitApiInstance: Api by lazy {
        retrofit.create(Api::class.java)
    }
}