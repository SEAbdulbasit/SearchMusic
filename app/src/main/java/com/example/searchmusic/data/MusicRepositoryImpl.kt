package com.example.searchmusic.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.network.MusicApiService
import com.example.searchmusic.domain.MusicRepository
import kotlinx.coroutines.flow.Flow

class MusicRepositoryImpl(
    private val database: MusicDatabase,
    private val apiService: MusicApiService
) : MusicRepository {

    override suspend fun getSearchFlow(query: String): Flow<PagingData<MusicEntity>> {
        val pagingSourceFactory = { database.musicDao().searchMusic(query) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(NETWORK_PAGE_SIZE, enablePlaceholders = false),
            initialKey = null,
            remoteMediator = MusicMediator(
                query = query,
                service = apiService,
                musicDatabase = database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}

