package com.example.searchmusic.presentation.musicdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.searchmusic.databinding.FragmentItemDetailBinding
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

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var exoPlayer: ExoPlayer

    private val viewModel: MusicDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setArguments(args: Bundle?) {
        if (args != null) {
            super.setArguments(Bundle(args).apply {
                putBundle("BUNDLE_ARGS", args)
            })
        } else {
            super.setArguments(null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        bindState(viewModel.state)
    }

    private fun bindState(uiState: SharedFlow<MusicDetailScreenState>) {
        lifecycleScope.launchWhenResumed {
            uiState.distinctUntilChanged().collect { musicDetailsState ->
                bindUiState(musicDetailsState.uiMModel)
                bindExoPlayer(musicDetailsState.uiMModel)
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

    private fun bindUiState(uiModel: MusicUiModel) {
        Glide.with(binding.image).load(uiModel.imageUrl).into(binding.image)
        binding.musicTitle.text = uiModel.musicTitle
        binding.artisName.text = uiModel.artisName
        binding.epVideoView.setShowNextButton(false)
        binding.epVideoView.setShowPreviousButton(false)
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

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.release()
        _binding = null
    }
}