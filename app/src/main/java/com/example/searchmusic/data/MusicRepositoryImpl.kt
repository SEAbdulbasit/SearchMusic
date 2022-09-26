package com.example.searchmusic.data

import androidx.paging.*
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.database.model.MusicKeys
import com.example.searchmusic.data.network.MusicApiService
import com.example.searchmusic.data.network.response.SearchResults
import com.example.searchmusic.domain.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val database: MusicDatabase, private val apiService: MusicApiService
) : MusicRepository {

    override suspend fun getSearchFlow(query: String): Flow<PagingData<MusicEntity>> {

        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.musicDao().searchMusic(dbQuery) }

        @OptIn(ExperimentalPagingApi::class) return Pager(
            config = PagingConfig(NETWORK_PAGE_SIZE, enablePlaceholders = false),
            initialKey = null,
            remoteMediator = MusicMediator(
                query = query, apiService = apiService, database = database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun searchForMusic(query: String, offSet: Int, pageSize: Int): SearchResults =
        apiService.searchForMusic(query = query, offSet = offSet, limit = pageSize)

    override suspend fun insertAll(musicList: List<MusicEntity>) {
        database.musicDao().insertAll(musicList)
    }

    override suspend fun getMusic(musicId: Long): Flow<MusicEntity> {
        return database.musicDao().getMusic(musicId)
    }

    override suspend fun searchMusic(query: String): PagingSource<Int, MusicEntity> {
        return database.musicDao().searchMusic(query)
    }

    override suspend fun clearMusic() {
        return database.musicDao().clearMusic()
    }

    override suspend fun insertAllKeys(remoteKey: List<MusicKeys>) {
        database.musicKeyDao().insertAll(remoteKey)
    }

    override suspend fun getMusicKey(musicId: Long): MusicKeys? {
        return database.musicKeyDao().getMusicKey(musicId)
    }

    override suspend fun clearKeys() {
        return database.musicKeyDao().clearKeys()
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}

