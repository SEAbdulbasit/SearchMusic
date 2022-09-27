package com.example.searchmusic.data

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.network.MusicApiService
import com.example.searchmusic.presentation.musiclist.DEFAULT_QUERY
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import java.net.UnknownHostException

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalPagingApi::class)
class MusicMediatorTest {

    private val repository: MusicApiService = mockk()
    private val db: MusicDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(), MusicDatabase::class.java
    ).build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlocking {
        coEvery { repository.searchForMusic(any(), any(), any()) } returns getSearchResults()

        val remoteMediator = MusicMediator(
            apiService = repository, database = db, query = DEFAULT_QUERY
        )
        val pagingState = PagingState<Int, MusicEntity>(
            listOf(), null, PagingConfig(10), 10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertEquals(true, true)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runBlocking {
        coEvery { repository.searchForMusic(any(), any(), any()) } returns getEmptySearchResults()

        val remoteMediator = MusicMediator(
            apiService = repository, database = db, query = DEFAULT_QUERY
        )
        val pagingState = PagingState<Int, MusicEntity>(
            listOf(), null, PagingConfig(10), 10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshLoadErrorWhenApiServiceThrowsError() = runBlocking {
        val unknownHostException = UnknownHostException()
        coEvery { repository.searchForMusic(any(), any(), any()) } throws unknownHostException

        val remoteMediator = MusicMediator(
            apiService = repository, database = db, query = DEFAULT_QUERY
        )
        val pagingState = PagingState<Int, MusicEntity>(
            listOf(), null, PagingConfig(10), 10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }
}