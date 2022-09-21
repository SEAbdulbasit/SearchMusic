package com.example.searchmusic.data

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.example.searchmusic.data.MusicRepositoryImpl.Companion.NETWORK_PAGE_SIZE
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.database.model.MusicKeys
import com.example.searchmusic.domain.MusicRepository
import retrofit2.HttpException
import java.io.IOException
//
//const val Music_STARTING_PAGE_INDEX = 0
//
//class MusicMediator(
//    private val query: String,
//    private val repository: MusicRepository
//) : PagingSource<Int, MusicEntity>() {
//    override fun getRefreshKey(state: PagingState<Int, MusicEntity>): Int? {
//        when (state.) {
//
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MusicEntity> {
//
//
//    }
//
//
//}
