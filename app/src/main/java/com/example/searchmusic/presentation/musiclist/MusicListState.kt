package com.example.searchmusic.presentation.musiclist

data class MusicScreenState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)

data class MusicUiModel(
    val trackId: Long,
    val musicTitle: String,
    val albumName: String,
    val artisName: String,
    val imageUrl: String,
    val previewUrl: String
)

fun getEmptyMusicUiModel() =
    MusicUiModel(
        trackId = -1,
        musicTitle = "",
        albumName = "",
        artisName = "",
        imageUrl = "",
        previewUrl = ""
    )