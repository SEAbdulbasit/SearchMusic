package com.example.searchmusic.data

import androidx.paging.*
import androidx.room.withTransaction
import com.example.searchmusic.data.MusicRepositoryImpl.Companion.NETWORK_PAGE_SIZE
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.database.model.MusicKeys
import com.example.searchmusic.data.network.MusicApiService
import retrofit2.HttpException
import java.io.IOException

const val Music_STARTING_PAGE_INDEX = 0

@OptIn(ExperimentalPagingApi::class)
class MusicMediator(
    private val query: String,
    private val service: MusicApiService,
    private val musicDatabase: MusicDatabase
) : RemoteMediator<Int, MusicEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MusicEntity>,
    ): MediatorResult {
        val page = when (loadType) {
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
            val apiResponse =
                service.searchForMusic(query = query, offSet = page, state.config.pageSize)

            val musicList = apiResponse.results ?: emptyList()
            val endOfPaginationReached = musicList.isEmpty()
            musicDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    musicDatabase.musicDao().clearMusic()
                    musicDatabase.musicKeyDao().clearKeys()
                }
                val prevKey =
                    if (page == Music_STARTING_PAGE_INDEX) null else page - NETWORK_PAGE_SIZE
                val nextKey = if (endOfPaginationReached) null else page + NETWORK_PAGE_SIZE

                val keys = musicList.map {
                    MusicKeys(id = it.artistId.toLong(), prevKey = prevKey, nextKey = nextKey)
                }
                val musicEntitiesList = musicList.map {
                    MusicEntity(
                        id = it.artistId.toLong(),
                        musicTitle = it.collectionName,
                        artisName = it.artistName,
                        albumName = it.primaryGenreName,
                        imageUrl = it.previewUrl

                    )
                }
                musicDatabase.musicDao().insertAll(musicEntitiesList)
                musicDatabase.musicKeyDao().insertAll(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MusicEntity>
    ): MusicKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                musicDatabase.musicKeyDao().getMusicKey(repoId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MusicEntity>): MusicKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                musicDatabase.musicKeyDao().getMusicKey(repo.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MusicEntity>): MusicKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                musicDatabase.musicKeyDao().getMusicKey(repo.id)
            }
    }
}
