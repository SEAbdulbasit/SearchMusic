package com.example.searchmusic.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.data.network.MusicApiService
import com.example.searchmusic.presentation.musiclist.DEFAULT_QUERY
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MusicRepositoryImplTest {

    private val database: MusicDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(), MusicDatabase::class.java
    ).build()

    private val apiService: MusicApiService = mockk()
    private lateinit var SUT: MusicRepositoryImpl

    @Before
    fun setUp() {
        SUT = MusicRepositoryImpl(
            database = database, apiService = apiService
        )
    }

    @Test
    fun dataIsInsertedIntoDatabaseWhenApiReturnsData() = runTest {
        coEvery { apiService.searchForMusic(any(), any(), any()) } returns getSearchResults()

        val entitiesList = apiService.searchForMusic(DEFAULT_QUERY, 0, 20).results?.map {
            MusicEntity(
                trackId = it.trackId,
                musicTitle = it.collectionName,
                artisName = it.artistName,
                albumName = it.primaryGenreName,
                imageUrl = it.artworkUrl100,
                previewUrl = it.previewUrl
            )
        } ?: emptyList()
        database.musicDao().insertAll(entitiesList)
        advanceUntilIdle()
        val dbItems = database.musicDao().getAll()
        assertEquals(entitiesList.size, dbItems.size)
    }


    @Test
    fun musicDetailsIsReturnedWhereThereIsData() = runTest {
        coEvery { apiService.searchForMusic(any(), any(), any()) } returns getSearchResults()

        val entitiesList = apiService.searchForMusic(DEFAULT_QUERY, 0, 20).results?.map {
            MusicEntity(
                trackId = it.trackId,
                musicTitle = it.collectionName,
                artisName = it.artistName,
                albumName = it.primaryGenreName,
                imageUrl = it.artworkUrl100,
                previewUrl = it.previewUrl
            )
        } ?: emptyList()
        database.musicDao().getMusic(1).first()
        advanceUntilIdle()
        assertEquals(1, entitiesList.first().trackId)
    }
}