package com.example.searchmusic.di

import android.content.Context
import androidx.room.Room
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.network.MusicApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMusicDao(database: MusicDatabase) = database.musicDao()

    @Singleton
    @Provides
    fun provideMusicKeysDao(database: MusicDatabase) = database.musicKeyDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): MusicDatabase =
        Room.databaseBuilder(
            context, MusicDatabase::class.java, "task-db"
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder().addInterceptor(logger).build()
    }

    @Singleton
    @Provides
    @Named("baseUrl")
    fun provideBaseURL(): String {
        return "https://itunes.apple.com/"

    }

    @Singleton
    @Provides
    fun apiServices(
        client: OkHttpClient, @Named("baseUrl") baseUrl: String

    ): MusicApiService {
        return Retrofit.Builder().baseUrl(baseUrl).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(MusicApiService::class.java)
    }

}
