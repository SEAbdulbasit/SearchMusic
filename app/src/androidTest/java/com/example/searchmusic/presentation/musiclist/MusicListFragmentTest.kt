package com.example.searchmusic.presentation.musiclist

import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.searchmusic.CountingIdlingResourceSingleton
import com.example.searchmusic.R
import com.example.searchmusic.data.FakeMusicRepository
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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppModule::class)
class MusicListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun before() {
        hiltRule.inject()
        IdlingRegistry.getInstance()
            .register(CountingIdlingResourceSingleton.countingIdlingResource)
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
    fun listIsShownWithInitialLoader() {
        launchFragmentInHiltContainer<MusicListFragment>(
            themeResId = R.style.Theme_SearchMusic
        )
        onView(withId(R.id.music_list)).check(matches(isDisplayed()))
        onView(withId(R.id.initial_loader)).check(matches(isDisplayed()))
    }

    @Test
    fun initialLoaderIsShownWhenSearchIsClicked() {
        launchFragmentInHiltContainer<MusicListFragment>(
            themeResId = R.style.Theme_SearchMusic
        )

        onView(withId(R.id.search_edit_text)).perform(typeText("Rih"))
        onView(withId(R.id.search)).perform(click())
        onView(withId(R.id.initial_loader)).check(matches(isDisplayed()))
    }

    @Test
    fun searchResultsAreShownWhenClickedSearch() = runBlocking {
        launchFragmentInHiltContainer<MusicListFragment>(
            themeResId = R.style.Theme_SearchMusic
        )

        onView(withId(R.id.search_edit_text)).perform(typeText("Rih"))
        onView(withId(R.id.search)).perform(click())

        onIdle()

        onView(withId(R.id.initial_loader)).check(matches(not(isDisplayed())))

        scrollAtAndCheckTestVisible(0, "Rihanna 1")
        scrollAtAndCheckTestVisible(1, "Rihanna 2")
        scrollAtAndCheckTestVisible(2, "Rihanna 3")

    }

    @Test
    fun emptyListTextIsShownWhenThereAreNotResults(): Unit = runBlocking {
        launchFragmentInHiltContainer<MusicListFragment>(
            themeResId = R.style.Theme_SearchMusic
        )

        onView(withId(R.id.search_edit_text)).perform(typeText("Ava"))
        onView(withId(R.id.search)).perform(click())

        onView(withText("No items found")).check(matches(isDisplayed()))

    }

    private fun scrollAtAndCheckTestVisible(position: Int, text: String) {
        onView(withId(R.id.music_list)).perform(
            RecyclerViewActions.scrollToPosition<MusicListAdapter.ViewHolder>(position)
        )
        onView(withText(text)).check(matches(isDisplayed()))
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance()
            .unregister(CountingIdlingResourceSingleton.countingIdlingResource)
    }

}