package com.example.searchmusic.domain

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.database.model.MusicKeys
import com.example.searchmusic.data.network.response.SearchResults
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getSearchFlow(query: String): Flow<PagingData<MusicEntity>>
    suspend fun getDataBase(): MusicDatabase
    suspend fun searchForMusic(query: String, offSet: Int, pageSize: Int): SearchResults
    suspend fun insertAll(musicList: List<MusicEntity>)
    suspend fun searchMusic(query: String): PagingSource<Int, MusicEntity>
    suspend fun clearMusic()
    suspend fun insertAllKeys(remoteKey: List<MusicKeys>)
    suspend fun getMusicKey(musicId: Long): MusicKeys?
    suspend fun clearKeys()

}