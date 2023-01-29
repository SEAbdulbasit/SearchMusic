package com.example.searchmusic.composepresentationlayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.searchmusic.composepresentationlayer.MusicDetailsScreen
import com.example.searchmusic.composepresentationlayer.MusicListingScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.MusicListingScreen.route) {
        composable(route = Screens.MusicListingScreen.route) {
            MusicListingScreen(navController = navController)
        }
        composable(
            route = Screens.MusicDetailsScreen.route + "/music_id={music_id}",
            arguments = listOf(
                navArgument(name = "music_id") {
                    type = NavType.LongType
                    nullable = false
                }
            )
        ) { entry ->
            MusicDetailsScreen(navController)
        }
    }
}