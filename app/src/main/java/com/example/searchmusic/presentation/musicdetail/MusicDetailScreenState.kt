package com.example.searchmusic.presentation.musicdetail

import com.example.searchmusic.presentation.musiclist.MusicUiModel
import com.example.searchmusic.presentation.musiclist.getEmptyMusicUiModel
import com.google.android.exoplayer2.ExoPlayer

data class MusicDetailScreenState(
    var exoPlayer: ExoPlayer,
    val uiMModel: MusicUiModel = getEmptyMusicUiModel()
)
