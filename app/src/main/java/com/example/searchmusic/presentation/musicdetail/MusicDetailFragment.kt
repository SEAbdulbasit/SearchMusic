package com.example.searchmusic.presentation.musicdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.searchmusic.App.Companion.mCacheDataSourceFactory
import com.example.searchmusic.R
import com.example.searchmusic.databinding.FragmentMusicDetailBinding
import com.example.searchmusic.presentation.musiclist.MusicUiModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MusicDetailFragment : Fragment() {

    private var _binding: FragmentMusicDetailBinding? = null
    private val binding get() = _binding!!
    private var mediaWasPlaying = true
    private var playedPosition = 0L

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: MusicDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaWasPlaying = savedInstanceState?.getBoolean(IS_MEDIA_PLAYING, true) ?: true
        playedPosition = savedInstanceState?.getLong(KEY_POSITION, 0L) ?: 0L

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindState(viewModel.state)
    }

    private fun bindState(uiState: SharedFlow<MusicDetailScreenState>) {
        lifecycleScope.launch {
            uiState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { musicDetailsState ->
                    bindScreen(musicDetailsState.uiMModel)
                }
        }
    }

    private fun bindScreen(uiModel: MusicUiModel) {
        if (uiModel.artisName.isNotEmpty()) {
            bindMusicDetail(uiModel)
            bindExoPlayer(uiModel)
        } else {
            bindEmptyState()
        }
    }

    private fun bindMusicDetail(uiModel: MusicUiModel) {
        with(binding) {
            musicLogo.isVisible = false
            musicDetailView.isVisible = true
            Glide.with(image).load(uiModel.imageUrl).placeholder(R.drawable.image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(image)
            musicTitle.text = uiModel.musicTitle
            artisName.text = uiModel.artisName
            albumName.text = uiModel.albumName
        }
    }

    private fun bindExoPlayer(uiModel: MusicUiModel) {
        val exoPlayer = getExoPlayerWithCache(uiModel.previewUrl)
        if (mediaWasPlaying) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
        exoPlayer.seekTo(playedPosition)
        exoPlayer.playWhenReady = mediaWasPlaying
        binding.epAudioView.player = exoPlayer
        binding.epAudioView.setShowNextButton(false)
        binding.epAudioView.setShowPreviousButton(false)
    }

    private fun bindEmptyState() {
        with(binding) {
            musicDetailView.isVisible = false
            musicLogo.isVisible = true
        }
    }

    private fun getExoPlayerWithCache(previewUrl: String): ExoPlayer {
        val exoPlayer = ExoPlayer.Builder(requireContext()).build()
        val mediaItem: MediaItem = MediaItem.fromUri(previewUrl)
        val mediaSource =
            ProgressiveMediaSource.Factory(mCacheDataSourceFactory).createMediaSource(mediaItem)
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        return exoPlayer
    }

    override fun onPause() {
        super.onPause()
        binding.epAudioView.player?.let { player ->
            mediaWasPlaying = player.isPlaying
            player.pause()

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.epAudioView.player?.let { player ->
            outState.putBoolean(IS_MEDIA_PLAYING, mediaWasPlaying)
            outState.putLong(KEY_POSITION, player.currentPosition)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.epAudioView.player?.release()
        binding.epAudioView.player = null
        _binding = null
    }
}

const val IS_MEDIA_PLAYING = "is_media_playing"
const val KEY_POSITION = "key_position"
