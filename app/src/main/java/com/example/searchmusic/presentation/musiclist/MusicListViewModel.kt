package com.example.searchmusic.presentation.musiclist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.searchmusic.domain.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MusicRepository,
) : ViewModel() {

    val state: StateFlow<MusicScreenState>
    val pagingDataFlow: Flow<PagingData<MusicUiModel>>
    private val actionStateFlow = MutableSharedFlow<MusicListActions>()

    init {
        val initialQuery: String = savedStateHandle[LAST_SEARCH_QUERY] ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle[LAST_QUERY_SCROLLED] ?: DEFAULT_QUERY

        val searches =
            actionStateFlow.filterIsInstance<MusicListActions.Search>().distinctUntilChanged()
                .onStart { emit(MusicListActions.Search(query = initialQuery)) }

        val scrolled =
            actionStateFlow.filterIsInstance<MusicListActions.Scroll>().distinctUntilChanged()
                .shareIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                    replay = 1
                ).onStart { emit(MusicListActions.Scroll(query = lastQueryScrolled)) }

        pagingDataFlow =
            searches.flatMapLatest { searchMusic(queryString = it.query) }.cachedIn(viewModelScope)
                .catch {

                }

        state = combine(searches, scrolled) { search, scrolled ->
            MusicScreenState(
                query = search.query,
                lastQueryScrolled = scrolled.query,
                hasNotScrolledForCurrentSearch = search.query != scrolled.query
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = MusicScreenState()
        )
    }

    fun processActions(actions: MusicListActions) {
        viewModelScope.launch { actionStateFlow.emit(actions) }
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    private suspend fun searchMusic(queryString: String): Flow<PagingData<MusicUiModel>> =
        repository.getSearchFlow(queryString).map { pagingData ->
            pagingData.map {
                MusicUiModel(
                    trackId = it.trackId,
                    artisName = it.artisName,
                    musicTitle = it.musicTitle,
                    albumName = it.albumName,
                    imageUrl = it.imageUrl,
                    previewUrl = it.previewUrl
                )
            }
        }.flowOn(Dispatchers.IO)

}

private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"
const val DEFAULT_QUERY = "rihanna"
