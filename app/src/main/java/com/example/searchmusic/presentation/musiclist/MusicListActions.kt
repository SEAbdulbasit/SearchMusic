package com.example.searchmusic.presentation.musiclist

sealed interface MusicListActions {
    data class Search(val query: String) : MusicListActions
    data class Scroll(val query: String) : MusicListActions
}