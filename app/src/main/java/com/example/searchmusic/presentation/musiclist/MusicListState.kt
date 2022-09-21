package com.example.searchmusic.presentation.musiclist

data class MusicScreenState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)


data class MusicUiModel(
    val id: Long,
    val songName: String,
    val albumName: String,
    val artisName: String,
    val imageUrl: String
)