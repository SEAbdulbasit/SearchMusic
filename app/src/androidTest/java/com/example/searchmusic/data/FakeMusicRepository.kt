package com.example.searchmusic.data

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.paging.LimitOffsetPagingSource
import com.example.searchmusic.CountingIdlingResourceSingleton
import com.example.searchmusic.data.database.MusicDao
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.database.model.MusicKeys
import com.example.searchmusic.data.network.response.SearchResults
import com.example.searchmusic.domain.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeMusicRepository(
) : MusicRepository {
    private val countingIdlingResource = CountingIdlingResourceSingleton

    override suspend fun getSearchFlow(query: String): Flow<PagingData<MusicEntity>> {
        try {
            countingIdlingResource.increment()
            return flowOf(PagingData.from(listOfMusicEntities.filter { it.artisName.contains(query) }))
        } finally {
            countingIdlingResource.decrement()
        }
    }

    override suspend fun searchForMusic(query: String, offSet: Int, pageSize: Int): SearchResults {
        return getSearchResults()
    }

    override suspend fun insertAll(musicList: List<MusicEntity>) {
    }

    override suspend fun getMusic(musicId: Long): Flow<MusicEntity> {
        try {
            countingIdlingResource.increment()
            return flowOf(listOfMusicEntities.first())
        } finally {
            countingIdlingResource.decrement()
        }

    }

    override suspend fun searchMusic(query: String): PagingSource<Int, MusicEntity> {

        //we wont be calling that so casting it to PagingSource
        return PagingData.from(listOfMusicEntities) as PagingSource<Int, MusicEntity>
    }

    override suspend fun clearMusic() {
    }

    override suspend fun insertAllKeys(remoteKey: List<MusicKeys>) {
        listOf(MusicKeys(id = 1L, prevKey = null, nextKey = null))
    }

    override suspend fun getMusicKey(musicId: Long): MusicKeys? {
        return null
    }

    override suspend fun clearKeys() {
    }
}