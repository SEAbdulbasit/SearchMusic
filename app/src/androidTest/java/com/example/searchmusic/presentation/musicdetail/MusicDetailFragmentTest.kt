package com.example.searchmusic.presentation.musicdetail

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.searchmusic.R
import com.example.searchmusic.launchFragmentInHiltContainer
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MusicDetailFragmentTest {

    @Test
    fun detailsViewIsNotVisibleWhenLaunched() {
        launchFragmentInHiltContainer<MusicDetailFragment>(
            themeResId = R.style.Theme_SearchMusic
        )
        onView(withId(R.id.music_detail_view)).check(matches(not(isDisplayed())))
    }

    @Test
    fun imageLogoIsVisibleWhenLaunched() {
        launchFragmentInHiltContainer<MusicDetailFragment>()
        onView(withId(R.id.music_logo)).check(matches(isDisplayed()))

    }
}