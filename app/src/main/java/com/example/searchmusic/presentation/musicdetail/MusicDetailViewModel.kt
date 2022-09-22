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
import javax.inject.Inject

@HiltViewModel
class MusicDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val repository: MusicRepository
) : ViewModel() {

    val state: StateFlow<MusicDetailScreenState>
    private val actionStateFlow = MutableSharedFlow<MusicDetailsActions>()

    init {
        val musicId: Long = savedStateHandle[MusicDetailFragment.ARG_ITEM_ID]
            ?: throw java.lang.IllegalStateException("Music id can not be null")

        val getMusicDetails =
            actionStateFlow.filterIsInstance<MusicDetailsActions.GetMusicDetails>()
                .onStart { emit(MusicDetailsActions.GetMusicDetails(musicId)) }

        val musicDetails = getMusicDetails.flatMapLatest { getMusicDetails(it.id) }

        state = musicDetails.map { music ->
            MusicDetailScreenState(
                uiMModel = music
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = MusicDetailScreenState()
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
        super.onCleared()
        viewModelScope.cancel()
    }
}
