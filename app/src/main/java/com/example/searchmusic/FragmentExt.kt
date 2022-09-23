package com.example.searchmusic

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavController

fun Fragment.navigateSafe(navController: NavController, navigationId: Int, bundle: Bundle) {
    if (canNavigate(navController)) navController.navigate(navigationId, bundle)
}

fun Fragment.canNavigate(navController: NavController): Boolean {
    val destinationIdInNavController = navController.currentDestination?.id

    val destinationIdOfThisFragment =
        view?.getTag(R.id.tag_navigation_destination_id) ?: destinationIdInNavController

    // check that the navigation graph is still in 'this' fragment, if not then the app already navigated:
    return if (destinationIdInNavController == destinationIdOfThisFragment) {
        view?.setTag(R.id.tag_navigation_destination_id, destinationIdOfThisFragment)
        true
    } else {
        Log.d("FragmentExt", "Cant navigate from this fragment")
        false
    }
}
