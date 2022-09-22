package com.example.searchmusic.presentation.musicdetail

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.searchmusic.domain.MusicRepository
import com.example.searchmusic.presentation.musiclist.MusicListActions
import com.example.searchmusic.presentation.musiclist.MusicScreenState
import com.example.searchmusic.presentation.musiclist.MusicUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MusicRepository
) : ViewModel() {

    private val actionStateFlow = MutableSharedFlow<MusicDetailsActions>()

    init {
        val musicId: Long = savedStateHandle[MusicDetailFragment.ARG_ITEM_ID]
            ?: throw IllegalArgumentException("Article ID required")


    }

    override fun onCleared() {
        super.onCleared()
    }
}

private const val LAST_VIEWED_ITEM: String = "last_viewed_item"
