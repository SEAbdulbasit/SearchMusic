package com.example.searchmusic.di

import android.app.Application
import com.example.searchmusic.data.MusicRepositoryImpl
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.network.MusicApiService
import com.example.searchmusic.domain.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModules {

    @Singleton
    @Provides
    fun provideMusicRepo(
        database: MusicDatabase, apiService: MusicApiService
    ): MusicRepository {
        return MusicRepositoryImpl(database = database, apiService = apiService)
    }
}