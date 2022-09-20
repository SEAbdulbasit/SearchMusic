package com.example.searchmusic.domain

import androidx.paging.PagingData
import com.example.searchmusic.data.database.model.MusicEntity
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun getSearchFlow(query: String): Flow<PagingData<MusicEntity>>
}