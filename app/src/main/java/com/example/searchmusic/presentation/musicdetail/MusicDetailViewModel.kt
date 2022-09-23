package com.example.searchmusic.presentation.musicdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.searchmusic.domain.MusicRepository
import com.example.searchmusic.presentation.musiclist.MusicUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, private val repository: MusicRepository
) : ViewModel() {

    val state: StateFlow<MusicDetailScreenState>
    private val actionStateFlow = MutableSharedFlow<MusicDetailsActions>()

    init {
        val musicId: Long = savedStateHandle[MUSIC_ID] ?: -1

        val getMusicDetails =
            actionStateFlow.filterIsInstance<MusicDetailsActions.GetMusicDetails>()
                .onStart { emit(MusicDetailsActions.GetMusicDetails(musicId)) }

        val musicDetails =
            getMusicDetails.filter { it.id != -1L }.flatMapLatest { getMusicDetails(it.id) }

        state = musicDetails.map { music ->
            MusicDetailScreenState(
                uiMModel = music
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = MusicDetailScreenState()
        )

        viewModelScope.launch {
            musicId?.let {
                actionStateFlow.emit(MusicDetailsActions.GetMusicDetails(it))
            }
        }
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
        viewModelScope.cancel()
        super.onCleared()
    }
}

const val MUSIC_ID = "music_id"
