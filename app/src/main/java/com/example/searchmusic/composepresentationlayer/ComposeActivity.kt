package com.example.searchmusic.composepresentationlayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.searchmusic.composepresentationlayer.navigation.Navigation
import com.example.searchmusic.composepresentationlayer.theme.SearchMusicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchMusicTheme {
                Navigation()
            }
        }
    }
}