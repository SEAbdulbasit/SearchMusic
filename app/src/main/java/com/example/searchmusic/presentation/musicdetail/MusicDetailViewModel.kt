package com.example.searchmusic.presentation.musicdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.searchmusic.domain.MusicRepository
import com.example.searchmusic.presentation.MediaPlayerServices
import com.example.searchmusic.presentation.musiclist.MusicUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MusicDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MusicRepository,
    private val mediaPlayerService: MediaPlayerServices
) : ViewModel() {

    private val keyPosition = savedStateHandle.get<Long>(KEY_POSITION) ?: 0
    val state: StateFlow<MusicDetailScreenState>

    init {
        val musicDetails = savedStateHandle.getStateFlow<Long>(MUSIC_ID, -1).filter { it != -1L }
            .flatMapLatest { getMusicDetails(it) }.onEach { musicUiModel ->
                mediaPlayerService.playMedia(musicUiModel.previewUrl, keyPosition)
            }

        state = musicDetails.map { music ->
            MusicDetailScreenState(
                uiMModel = music, exoPlayer = mediaPlayerService.exoPlayer
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = MusicDetailScreenState(exoPlayer = mediaPlayerService.exoPlayer)
        )
    }

    private suspend fun getMusicDetails(musicId: Long): Flow<MusicUiModel> {
        return repository.getMusic(musicId).map {
            MusicUiModel(
                trackId = it.trackId,
                musicTitle = it.musicTitle,
                albumName = it.albumName,
                artisName = it.artisName,
                imageUrl = it.imageUrl.replace("100", "460"),
                previewUrl = it.previewUrl
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun onCleared() {
        savedStateHandle[MUSIC_ID] = state.value.uiMModel.trackId
        savedStateHandle[KEY_POSITION] = state.value.exoPlayer.currentPosition
        mediaPlayerService.exoPlayer.release()
        viewModelScope.cancel()
        super.onCleared()
    }
}

const val MUSIC_ID = "music_id"
const val KEY_POSITION = "key_position"
