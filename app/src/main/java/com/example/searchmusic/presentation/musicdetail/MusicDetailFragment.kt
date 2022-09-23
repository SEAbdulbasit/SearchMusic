package com.example.searchmusic.presentation.musicdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.searchmusic.R
import com.example.searchmusic.databinding.FragmentMusicDetailBinding
import com.example.searchmusic.presentation.musiclist.MusicUiModel
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged


@AndroidEntryPoint
class MusicDetailFragment : Fragment() {

    private var _binding: FragmentMusicDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var exoPlayer: ExoPlayer

    private val viewModel: MusicDetailViewModel by viewModels()

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
        lifecycleScope.launchWhenResumed {
            uiState.distinctUntilChanged().collect { musicDetailsState ->
                bindScreen(musicDetailsState.uiMModel)
            }
        }
    }

    private fun bindExoPlayer(uiMModel: MusicUiModel) {
        uiMModel.previewUrl.trim().let {
            if (it.isNotEmpty()) {
                play(it)
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
            musicDetailView.isVisible = true
            Glide.with(image).load(uiModel.imageUrl).placeholder(R.drawable.image_placeholder)
                .into(image)
            musicTitle.text = uiModel.musicTitle
            artisName.text = uiModel.artisName
            epVideoView.setShowNextButton(false)
            epVideoView.setShowPreviousButton(false)
            albumName.text = uiModel.albumName
        }
    }

    private fun bindEmptyState() {
        with(binding) {
            musicDetailView.isVisible = false
            musicLogo.isVisible = true
        }
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(requireContext())
        val loadControl = DefaultLoadControl()
        val renderersFactory = DefaultRenderersFactory(requireContext())

        exoPlayer = ExoPlayer.Builder(requireContext()).setRenderersFactory(renderersFactory)
            .setLoadControl(loadControl).setTrackSelector(trackSelector).build()
        binding.epVideoView.player = exoPlayer
    }

    private fun play(videoUri: String) {
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.release()
        _binding = null
    }
}