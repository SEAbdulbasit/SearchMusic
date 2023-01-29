package com.example.searchmusic.composepresentationlayer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.searchmusic.App
import com.example.searchmusic.R
import com.example.searchmusic.presentation.musicdetail.MusicDetailViewModel
import com.example.searchmusic.presentation.musiclist.MusicUiModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView


@Composable
fun MusicDetailsScreen(navController: NavController) {
    val viewModel = hiltViewModel<MusicDetailViewModel>()
    val state = viewModel.state.collectAsState()
    MusicDetailsScreenView(state.value.uiMModel, navController)
}

@Composable
fun MusicDetailsScreenView(musicUiModel: MusicUiModel, navController: NavController) {
    if (musicUiModel.previewUrl.isNotEmpty()) {
        Column(
            Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(24.dp)
                        .clip(RoundedCornerShape(16.dp)), painter = rememberAsyncImagePainter(
                        musicUiModel.getLargePreview(),
                        placeholder = painterResource(id = R.drawable.image_placeholder)
                    ), contentDescription = "Album Cover"
                )

                Text(
                    text = musicUiModel.musicTitle,
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface)
                )
                Text(
                    "Album : ${musicUiModel.albumName}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    "Artist : ${musicUiModel.artisName}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            ExoPlayerView(musicUiModel, navController)
        }
    }

}

@Composable
private fun ExoPlayerView(
    musicUiModel: MusicUiModel, navController: NavController
) {
    val context = LocalContext.current

    val mExoPlayer = remember(context) {
        val exoPlayer = ExoPlayer.Builder(context).build()
        val mediaItem: MediaItem = MediaItem.fromUri(musicUiModel.previewUrl)
        val mediaSource =
            ProgressiveMediaSource.Factory(App.mCacheDataSourceFactory).createMediaSource(mediaItem)
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady
        exoPlayer
    }

    var exoPlayerView: PlayerControlView? = null

    // Implementing ExoPlayer
    AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
        PlayerControlView(context).apply {
            exoPlayerView = this
            setShowNextButton(false)
            setShowPreviousButton(false)
            showTimeoutMs = 0
            player = mExoPlayer
        }
    })


    val lifecycleOwner = LocalLifecycleOwner.current
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    clearExoPlayer(mExoPlayer = mExoPlayer, exoPlayerView = exoPlayerView)
                }
                else -> {}
            }
        }

    })

    BackHandler {
        clearExoPlayer(mExoPlayer = mExoPlayer, exoPlayerView = exoPlayerView)
        navController.popBackStack()
    }


}

data class ExoPlayerState(
    val seekTime: Long,
    val isPlaying: Boolean
)

fun clearExoPlayer(mExoPlayer: ExoPlayer, exoPlayerView: PlayerControlView?) {
    mExoPlayer.pause()
    exoPlayerView?.player = null
    mExoPlayer.release()
}

