package com.example.searchmusic.presentation

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class MediaPlayerServices(context: Context) {
    val exoPlayer: ExoPlayer

    init {
        exoPlayer = ExoPlayer.Builder(context).build()
    }

    fun playMedia(mediaUrl: String, keyPosition: Long) {
        val mediaItem: MediaItem = MediaItem.fromUri(mediaUrl)
        exoPlayer.let { player ->
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
            player.seekTo(keyPosition)
            player.play()
        }
    }

}