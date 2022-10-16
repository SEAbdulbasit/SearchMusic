package com.example.searchmusic.presentation

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class MediaPlayerService : Service() {
    var exoPlayer: ExoPlayer? = null

    private var binder: MediaServiceBinder = MediaServiceBinder()
    private var lastPlayAudioUrl = ""

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("MediaPlayerService", "New Command Triggered")
        val media = intent.getStringExtra(MEDIA) ?: ""
        val seekTo = intent.getLongExtra(SEEK_TO, 0L)

        if (!(media.isEmpty() || media == lastPlayAudioUrl)) {
            lastPlayAudioUrl = media
            playExoMedia(media, seekTo)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun playExoMedia(mediaUrl: String, keyPosition: Long) {
        val mediaItem: MediaItem = MediaItem.fromUri(mediaUrl)
        exoPlayer.let { player ->
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
            player?.seekTo(keyPosition)
        }
    }

    inner class MediaServiceBinder : Binder() {
        fun getExoPlayer() = exoPlayer
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        Log.d("MediaPlayerService", "I am destroyed")
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }
}

const val SEEK_TO = "seek_to"
const val MEDIA = "media"