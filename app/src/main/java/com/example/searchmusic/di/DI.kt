package com.example.searchmusic.di

import android.app.Application
import androidx.room.Room
import com.example.searchmusic.data.MusicRepositoryImpl
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.network.MusicApiService
import com.example.searchmusic.domain.MusicRepository
import com.example.searchmusic.presentation.MediaPlayerServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun provideSummitsRepository(repo: MusicRepositoryImpl): MusicRepository

    companion object {
        @Provides
        fun provideMusicDao(database: MusicDatabase) = database.musicDao()

        @Provides
        fun provideMusicKeysDao(database: MusicDatabase) = database.musicKeyDao()

        @Singleton
        @Provides
        fun provideAppDatabase(context: Application): MusicDatabase = Room.databaseBuilder(
            context, MusicDatabase::class.java, "music-db"
        ).fallbackToDestructiveMigration().build()

        @Singleton
        @Provides
        fun apiServices(client: OkHttpClient): MusicApiService {
            return Retrofit.Builder().baseUrl("https://itunes.apple.com/").client(client)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(MusicApiService::class.java)
        }
    }

}

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelMovieModule {
    @Provides
    @ViewModelScoped
    fun provideMediaPlayServices(context: Application): MediaPlayerServices {
        return MediaPlayerServices(context)
    }
}
