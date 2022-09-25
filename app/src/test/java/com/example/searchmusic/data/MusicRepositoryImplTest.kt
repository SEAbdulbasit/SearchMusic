package com.example.searchmusic.data

import app.cash.turbine.test
import com.example.searchmusic.data.database.MusicDatabase
import com.example.searchmusic.data.network.MusicApiService
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class MusicRepositoryImplTest {

    private val database: MusicDatabase = mockk()
    private val apiService: MusicApiService = mockk()

    private lateinit var SUT: MusicRepositoryImpl

    @Before
    fun setUp() {
        SUT = MusicRepositoryImpl(
            database = database, apiService = apiService
        )
    }

    @Test
    fun `given SearchFor Music invoked, music list should be returned`() = runTest {
        coEvery {
            apiService.searchForMusic(
                query = any(), offSet = any(), limit = any()
            )
        } returns searchResults

        val results = SUT.searchForMusic(query = query, offSet = initialOffset, pageSize = 3)

        assertEquals(results.results?.size, pageSize)
    }

    @Test
    fun `given SearchFor Music invoked, when searchForMusic throws exception, exception should be thrown`() =
        runTest {
            val exception = UnknownHostException()

            coEvery {
                apiService.searchForMusic(
                    query = any(), offSet = any(), limit = any()
                )
            } throws exception

            val results = runCatching {
                SUT.searchForMusic(
                    query = query, offSet = initialOffset, pageSize = 3
                )
            }
            assertEquals(exception.javaClass, results.exceptionOrNull()?.javaClass)
        }

    @Test
    fun `given getMusic Music invoked, then return MusicEntity`() = runTest {
        val musicEntity = listOfMusic.first()
        coEvery {
            database.musicDao().getMusic(any())
        } returns flowOf(musicEntity)

        SUT.getMusic(musicEntity.trackId).test {
            val item = awaitItem()
            assertEquals(musicEntity.trackId, item.trackId)
            awaitComplete()
        }
    }

    @Test
    fun `given insertAll, musicDao insertAll should be invoked`() = runTest {
        coEvery {
            database.musicDao().insertAll(any())
        } returns Unit

        val musicList = listOfMusic
        SUT.insertAll(musicList)
        verify { database.musicDao().insertAll(musicList) }
    }

    private val query = "Rihanna"
    private val initialOffset = 30
    private val pageSize = 3
}