package com.example.searchmusic.presentation.musicdetail

sealed interface MusicDetailsActions {
    data class GetMusicDetails(val id: Long) : MusicDetailsActions
}
