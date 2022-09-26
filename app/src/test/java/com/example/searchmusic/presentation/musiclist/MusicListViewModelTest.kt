package com.example.searchmusic.presentation.musiclist

import androidx.lifecycle.SavedStateHandle
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.example.searchmusic.data.database.model.toUiModel
import com.example.searchmusic.data.listOfMusic
import com.example.searchmusic.domain.MusicRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
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
    fun emptyStateShouldBeEmittedWhereViewModelIsInitialized() = runBlocking {
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
    fun stateShouldBeUpdatedWithLatestQueryUponClickingSearch() =
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

    @Test
    fun listShouldBeReturnedWhenThereIsData() = runTest {
        val musicList = listOfMusic
        val musicUiModelList = musicList.map { it.toUiModel() }

        coEvery { repository.getSearchFlow(DEFAULT_QUERY) } returns flowOf(
            PagingData.from(
                musicList
            )
        )

        val differ = AsyncPagingDataDiffer(
            diffCallback = MusicListAdapter.DIFF_CALLBACK,
            updateCallback = ListUpdateTestCallback(),
            workerDispatcher = Dispatchers.Main
        )

        SUT.pagingDataFlow.test {
            val firstItem = awaitItem()
            differ.submitData(firstItem)
            advanceUntilIdle()
            assertEquals(musicUiModelList, differ.snapshot().items)
        }
    }

    class ListUpdateTestCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}