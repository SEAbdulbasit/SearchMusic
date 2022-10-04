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
import com.example.searchmusic.R
import com.example.searchmusic.databinding.FragmentMusicDetailBinding
import com.example.searchmusic.presentation.musiclist.MusicUiModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MusicDetailFragment : Fragment() {

    private var _binding: FragmentMusicDetailBinding? = null
    private val binding get() = _binding!!
    private var exoPlayer: ExoPlayer? = null

    private val KEY_POSITION = "position"
    private val KEY_AUTO_PLAY = "auto_play"

    private var startAutoPlay = false
    private var startPosition: Long = 0

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: MusicDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAutoPlay = savedInstanceState?.getBoolean(KEY_AUTO_PLAY) ?: true
        startPosition = savedInstanceState?.getLong(KEY_POSITION) ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
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

    private fun bindExoPlayer(uiMModel: MusicUiModel) {
        uiMModel.previewUrl.trim().let {
            if (it.isNotEmpty()) {
                play(it)
            }
        }
    }

    private fun bindEmptyState() {
        with(binding) {
            musicDetailView.isVisible = false
            musicLogo.isVisible = true
        }
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.epAudioView.player = exoPlayer
        binding.epAudioView.setShowNextButton(false)
        binding.epAudioView.setShowPreviousButton(false)
    }

    private fun play(audioUrl: String) {
        val mediaItem: MediaItem = MediaItem.fromUri(audioUrl)
        exoPlayer?.let { player ->
            player.setMediaItem(mediaItem)
            player.prepare()
            player.seekTo(startPosition)
            player.playWhenReady = startAutoPlay
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        exoPlayer?.let { player ->
            outState.putBoolean(KEY_AUTO_PLAY, player.playWhenReady)
            outState.putLong(KEY_POSITION, player.currentPosition)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()
        _binding = null
    }
}
