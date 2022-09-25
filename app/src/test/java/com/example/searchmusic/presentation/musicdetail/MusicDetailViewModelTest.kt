package com.example.searchmusic.presentation.musicdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.searchmusic.data.database.model.MusicEntity
import com.example.searchmusic.domain.MusicRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class MusicDetailViewModelTest {

    private lateinit var SUT: MusicDetailViewModel

    private val repository: MusicRepository = mockk()

    private val map = mapOf(MUSIC_ID to 1441154571)
    private val savedInstanceStateHandle: SavedStateHandle = SavedStateHandle(map)

    @Before
    fun setUp() {
        SUT = MusicDetailViewModel(
            repository = repository, savedStateHandle = savedInstanceStateHandle
        )
    }

    @Test
    fun `given ViewModel initialise, then Empty state should be emitted`(): Unit = runBlocking {
        SUT = MusicDetailViewModel(
            repository = repository, savedStateHandle = savedInstanceStateHandle
        )

        SUT.state.test {
            val item = awaitItem()
            assertEquals(-1, item.uiMModel.trackId)
            assertEquals(true, item.uiMModel.musicTitle.isEmpty())
            assertEquals(true, item.uiMModel.artisName.isEmpty())
            assertEquals(true, item.uiMModel.albumName.isEmpty())
            assertEquals(true, item.uiMModel.previewUrl.isEmpty())
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val musicEntity = MusicEntity(
        trackId = 1441154571,
        artisName = "Rihanna",
        musicTitle = "Disturbia",
        albumName = "Good Girl Gone Bad: Reloaded",
        imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music126/v4/2b/c0/81/2bc081c8-25f0-ba43-d451-587a54613778/16UMGIM59202.rgb.jpg/100x100bb.jpg",
        previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/a4/16/ce/a416ce10-e861-6e5a-194d-ad4fb2e4cd38/mzaf_7519322396128489319.plus.aac.p.m4a"
    )

}