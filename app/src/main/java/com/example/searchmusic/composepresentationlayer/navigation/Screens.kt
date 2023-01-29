package com.example.searchmusic.composepresentationlayer.navigation

sealed class Screens(val route: String) {
    object MusicListingScreen : Screens("listing_screen")
    object MusicDetailsScreen : Screens("details_screen")

    fun withArgs(vararg value: String): String {
        return buildString {
            append(route)
            value.forEach {
                append("/$it")

            }
        }
    }
}