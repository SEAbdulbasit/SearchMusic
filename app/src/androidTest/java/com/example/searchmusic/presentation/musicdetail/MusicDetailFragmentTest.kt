package com.example.searchmusic.presentation.musicdetail

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.searchmusic.R
import com.example.searchmusic.data.FakeMusicRepository
import com.example.searchmusic.data.listOfMusicEntities
import com.example.searchmusic.di.AppModule
import com.example.searchmusic.domain.MusicRepository
import com.example.searchmusic.launchFragmentInHiltContainer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppModule::class)
class MusicDetailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun before() {
        hiltRule.inject()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object TestAppModule {

        @Provides
        fun provideNewsRepository(): MusicRepository {
            return FakeMusicRepository()
        }
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

    @Test
    fun musicDetailsAreShown(): Unit = runBlocking {

        val entity = listOfMusicEntities.first()

        launchFragmentInHiltContainer<MusicDetailFragment>(
            bundleOf(MUSIC_ID to entity.trackId)
        )

        onView(withId(R.id.music_detail_view)).check(matches(isDisplayed()))
        onView(withText(entity.musicTitle)).check(matches(isDisplayed()))
        onView(withText(entity.artisName)).check(matches(isDisplayed()))
        onView(withText(entity.albumName)).check(matches(isDisplayed()))
        onView(withId(R.id.image)).check(matches(isDisplayed()))
    }
}