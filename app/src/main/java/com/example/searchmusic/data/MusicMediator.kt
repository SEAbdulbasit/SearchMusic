package com.example.searchmusic.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.searchmusic.data.MusicRepositoryImpl.Companion.NETWORK_PAGE_SIZE
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.database.model.MusicKeys
import com.example.searchmusic.domain.MusicRepository
import retrofit2.HttpException
import java.io.IOException

const val Music_STARTING_PAGE_INDEX = 0

@OptIn(ExperimentalPagingApi::class)
class MusicMediator(
    private val query: String,
    private val repository: MusicRepository
) : RemoteMediator<Int, MusicEntity>() {

    var lastPageIndex = 0
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MusicEntity>,
    ): MediatorResult {
        var page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(NETWORK_PAGE_SIZE) ?: Music_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            if (page < lastPageIndex) {
                page = lastPageIndex
            }

            val apiResponse =
                repository.searchForMusic(query = query, offSet = page, state.config.pageSize)

            val musicList = apiResponse.results ?: emptyList()
            val endOfPaginationReached = musicList.size < NETWORK_PAGE_SIZE

            Log.d("MusicaMediator", "Items size: ${musicList.size}")

            repository.getDataBase().withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    Log.d("MusicaMediator", "Items size: flushing db")

                    repository.clearMusic()
                    repository.clearKeys()
                }
                val prevKey =
                    if (page == Music_STARTING_PAGE_INDEX) null else page - NETWORK_PAGE_SIZE
                val nextKey = if (endOfPaginationReached) null else page + NETWORK_PAGE_SIZE

                lastPageIndex = nextKey ?: 0

                Log.d("MusicaMediator", "Is end of pagination: ${endOfPaginationReached}")

                val keys = musicList.map {
                    MusicKeys(id = it.trackId, prevKey = prevKey, nextKey = nextKey)
                }
                val musicEntitiesList = musicList.map {
                    MusicEntity(
                        trackId = it.trackId,
                        musicTitle = it.collectionName,
                        artisName = it.artistName,
                        albumName = it.primaryGenreName,
                        imageUrl = it.artworkUrl100

                    )
                }
                repository.insertAll(musicEntitiesList)
                repository.insertAllKeys(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            exception.printStackTrace()
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            exception.printStackTrace()
            return MediatorResult.Error(exception)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MusicEntity>
    ): MusicKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.trackId?.let { trackingId ->
                repository.getMusicKey(trackingId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MusicEntity>): MusicKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { music ->
                repository.getMusicKey(music.trackId)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MusicEntity>): MusicKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { music ->
                repository.getMusicKey(music.trackId)
            }
    }
}
