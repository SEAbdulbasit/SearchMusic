package com.example.searchmusic.composepresentationlayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.example.searchmusic.composepresentationlayer.navigation.Screens
import com.example.searchmusic.composepresentationlayer.theme.SearchMusicTheme
import com.example.searchmusic.composepresentationlayer.utils.CombinedPreviews
import com.example.searchmusic.presentation.musiclist.MusicListActions
import com.example.searchmusic.presentation.musiclist.MusicListViewModel
import com.example.searchmusic.presentation.musiclist.MusicScreenState
import com.example.searchmusic.presentation.musiclist.MusicUiModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListingScreen(navController: NavController) {
    val viewModel = hiltViewModel<MusicListViewModel>()
    val state = viewModel.state.collectAsState()
    Scaffold(topBar = {
        SearchAppBar(state = state.value,
            onSearch = { viewModel.processActions(MusicListActions.Search(state.value.query)) },
            onValueChange = { viewModel.processActions(MusicListActions.OnQueryChange(it)) })
    }, content = {
        Body(it, viewModel, navController)
    })
}

@Composable
private fun Body(
    it: PaddingValues,
    viewModel: MusicListViewModel,
    navController: NavController
) {
    Box(modifier = Modifier.padding(it)) {
        MusicList(viewModel) { model ->
            navController.navigate(
                Screens.MusicDetailsScreen.withArgs(
                    "music_id=" +
                            model.trackId
                )
            )
        }
    }
}

@Composable
fun SearchAppBar(state: MusicScreenState, onSearch: () -> Unit, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(all = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Search", style = MaterialTheme.typography.headlineLarge)
            SearchTextField(value = state.query, onSearch = onSearch, onValueChange = onValueChange)
        }
    }
}


@Composable
fun MusicList(viewModel: MusicListViewModel, onItemClick: (MusicUiModel) -> Unit) {
    val musics = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val lazyColumnListState = rememberLazyListState()
    val state = viewModel.state.collectAsState()
    if (state.value.hasNotScrolledForCurrentSearch) {
        LaunchedEffect(key1 = Unit) {
            lazyColumnListState.scrollToItem(0)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(
            Modifier
                .padding(start = 16.dp, end = 16.dp)
                .weight(1f), state = lazyColumnListState
        ) {
            items(
                count = musics.itemCount,
            ) { position ->
                val item = musics[position]
                if (item != null) {
                    CardItem(musicModel = item, onItemClick = onItemClick)
                }
            }
        }
        musics.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                loadState.append is LoadState.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                loadState.append is LoadState.NotLoading -> {
                }
            }
        }
    }
}

@Composable
fun CardItem(musicModel: MusicUiModel, onItemClick: (MusicUiModel) -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clickable {
                onItemClick.invoke(musicModel)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = rememberAsyncImagePainter(musicModel.imageUrl),

            contentDescription = "Da",
            modifier = Modifier
                .requiredSize(48.dp)
                .clip(RoundedCornerShape(4.dp)),
        )

        Column(
            Modifier
                .padding(start = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = musicModel.musicTitle,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                "${musicModel.albumName}  ${musicModel.artisName}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 14.sp
                ),
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@CombinedPreviews
@Composable
fun showScreen() = SearchMusicTheme {
    //MusicListingScreen()
}

@CombinedPreviews
@Composable
fun showToolbar() = SearchMusicTheme {
    SearchAppBar(MusicScreenState("qf", "sdf", false), {}, {})
}

@CombinedPreviews
@Composable
fun showMusicItem() = SearchMusicTheme {
    CardItem(musicModel = MusicUiModel(
        1, "Music title", "Album", "Artis Name", "ImageUrk", "sdf"
    ), onItemClick = {})
}
