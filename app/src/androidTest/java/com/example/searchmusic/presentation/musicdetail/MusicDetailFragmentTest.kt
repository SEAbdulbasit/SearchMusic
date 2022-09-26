package com.example.searchmusic.presentation.musicdetail

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.searchmusic.R
import com.example.searchmusic.domain.MusicRepository
import com.example.searchmusic.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class MusicDetailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    val repository: MusicRepository = mockk()

    @Before
    fun before() {
        hiltRule.inject()
    }


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

//    @Test
//    fun musicDetailsAreShown() {
//        val entity = listOfMusicEntities.first()
//        coEvery { repository.getMusic(any()) } returns flow { emit(entity) }
//        launchFragmentInHiltContainer<MusicDetailFragment>(fragmentArgs = Bundle().also {
//            it.putLong(
//                MUSIC_ID, entity.trackId
//            )
//        })
//
//        onView(withId(R.id.music_detail_view)).check(matches(isDisplayed()))
//
//
//    }
}