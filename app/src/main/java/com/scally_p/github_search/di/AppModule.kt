package com.scally_p.github_search.di

import com.scally_p.github_search.data.local.repository.RepositoriesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRepository(): RepositoriesRepository = RepositoriesRepository()
}