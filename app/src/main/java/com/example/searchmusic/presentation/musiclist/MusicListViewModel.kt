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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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

        val textChanged =
            actionStateFlow.filterIsInstance<MusicListActions.OnQueryChange>()
                .distinctUntilChanged()
                .onStart { emit(MusicListActions.OnQueryChange(query = initialQuery)) }

        val scrolled =
            actionStateFlow.filterIsInstance<MusicListActions.Scroll>()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                    initialValue = MusicListActions.Scroll(lastQueryScrolled)
                )

        pagingDataFlow =
            searches.flatMapLatest { searchMusic(queryString = it.query) }.cachedIn(viewModelScope)
                .catch {

                }

        state = combine(searches, scrolled, textChanged) { search, scrolled, searchText ->
            MusicScreenState(
                query = searchText.query,
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

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        viewModelScope.cancel()
        super.onCleared()
    }

}

const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
const val LAST_SEARCH_QUERY: String = "last_search_query"
const val DEFAULT_QUERY = "rihanna"
