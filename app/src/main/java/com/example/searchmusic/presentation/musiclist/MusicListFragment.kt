package com.example.searchmusic.presentation.musiclist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmusic.R
import com.example.searchmusic.databinding.FragmentMusicListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicListFragment : Fragment() {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MusicListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        bindState(uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            view = itemDetailFragmentContainer,
            uiActions = {
                viewModel.processActions(it)
            })
    }

    private fun bindState(
        uiState: StateFlow<MusicScreenState>,
        pagingData: Flow<PagingData<MusicUiModel>>,
        uiActions: (MusicListActions) -> Unit,
        view: View?
    ) {
        val musicAdapter = MusicListAdapter(view)

        val adapter = musicAdapter.withLoadStateFooter(
            footer = MusicLoadStateAdapter { musicAdapter.retry() })

        binding.musicList.adapter = adapter

        bindSearch(
            uiState = uiState, onQueryChanged = uiActions
        )
        bindList(
            musicListAdapter = musicAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions,
        )
    }

    private fun bindSearch(
        uiState: StateFlow<MusicScreenState>, onQueryChanged: (MusicListActions.Search) -> Unit
    ) {

        binding.search.setOnClickListener {
            updateRepoListFromInput(onQueryChanged)
        }

        lifecycleScope.launch {
            uiState.map { it.query }.distinctUntilChanged().collect {
                binding.searchEditText.setText(it)
            }
        }
    }

    private fun updateRepoListFromInput(onQueryChanged: (MusicListActions.Search) -> Unit) {
        binding.searchEditText.text?.trim().let {
            if (it?.isNotEmpty() == true) {
                binding.musicList.scrollToPosition(0)
                onQueryChanged(MusicListActions.Search(query = it.toString()))
            }
        }
    }

    private fun bindList(
        musicListAdapter: MusicListAdapter,
        uiState: StateFlow<MusicScreenState>,
        pagingData: Flow<PagingData<MusicUiModel>>,
        onScrollChanged: (MusicListActions.Scroll) -> Unit
    ) {
        binding.btnRetry.setOnClickListener { musicListAdapter.retry() }
        binding.swipeRefresh.setOnRefreshListener { musicListAdapter.refresh() }

        binding.musicList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(MusicListActions.Scroll(query = uiState.value.query))
            }
        })

        val notLoading = musicListAdapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch =
            uiState.map { it.hasNotScrolledForCurrentSearch }.distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading, hasNotScrolledForCurrentSearch, Boolean::and
        ).distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(musicListAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) binding.musicList.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            musicListAdapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state

                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && musicListAdapter.itemCount == 0
                // show empty list
                binding.emptyList.isVisible = isListEmpty

                //only show swipe refreshing on
                if (loadState.mediator?.refresh != LoadState.Loading)
                    binding.swipeRefresh.isRefreshing =
                        loadState.mediator?.refresh is LoadState.Loading

                // Only show the list if refresh succeeds, either from the the local db or the remote.
                binding.musicList.isVisible =
                    loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading

                binding.btnRetry.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && musicListAdapter.itemCount == 0

                // Show loading spinner during initial load or refresh.
                binding.initialLoader.isVisible =
                    loadState.mediator?.refresh is LoadState.Loading && !(binding.swipeRefresh.isRefreshing)

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    it.error.printStackTrace()
                    Toast.makeText(
                        context, "Something went wrong", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}