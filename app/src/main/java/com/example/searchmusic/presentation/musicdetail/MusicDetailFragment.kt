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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MusicDetailFragment : Fragment() {

    private var _binding: FragmentMusicDetailBinding? = null
    private val binding get() = _binding!!
    private var mediaWasPlaying = false

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: MusicDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaWasPlaying = savedInstanceState?.getBoolean(IS_MEDIA_PLAYING, false) == true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer(viewModel.state)
        bindState(viewModel.state)
    }

    private fun initializePlayer(state: StateFlow<MusicDetailScreenState>) {
        binding.epAudioView.player = state.value.exoPlayer
        binding.epAudioView.setShowNextButton(false)
        binding.epAudioView.setShowPreviousButton(false)
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

    private fun bindEmptyState() {
        with(binding) {
            musicDetailView.isVisible = false
            musicLogo.isVisible = true
        }
    }

    override fun onPause() {
        super.onPause()
        mediaWasPlaying = binding.epAudioView.player?.isPlaying ?: false
        binding.epAudioView.player?.pause()

    }

    override fun onResume() {
        super.onResume()
        if (mediaWasPlaying)
            binding.epAudioView.player?.play()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_MEDIA_PLAYING, mediaWasPlaying)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

const val IS_MEDIA_PLAYING = "is_media_playing"