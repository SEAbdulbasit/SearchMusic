package com.example.searchmusic.presentation.musiclist

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.searchmusic.domain.MusicRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MusicListViewModelTest {

    private val savedInstanceStateHandle: SavedStateHandle = SavedStateHandle()
    private val repository: MusicRepository = mockk()
    private lateinit var SUT: MusicListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        SUT = MusicListViewModel(
            savedStateHandle = savedInstanceStateHandle, repository = repository
        )
    }

    @Test
    fun `given ViewModel initialize, empty state should be shown`() = runBlocking {

        SUT = MusicListViewModel(
            savedStateHandle = savedInstanceStateHandle, repository = repository
        ).also {
            it.state.test {
                val firstItem = awaitItem()
                assertEquals(false, firstItem.hasNotScrolledForCurrentSearch)
                assertEquals(DEFAULT_QUERY, firstItem.query)
            }
        }
    }

    @Test
    fun `given ViewModel initialized, when search is clicked, then  state should updated with latest query`() =
        runTest {
            SUT.state.test {
                val firstItem = awaitItem()
                assertEquals(false, firstItem.hasNotScrolledForCurrentSearch)
                SUT.processActions(MusicListActions.Search("Ava"))
                val secondItem = awaitItem()
                assertEquals("Ava", secondItem.query)
                assertEquals(true, secondItem.hasNotScrolledForCurrentSearch)
            }
        }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}