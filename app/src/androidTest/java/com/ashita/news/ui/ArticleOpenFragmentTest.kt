package com.ashita.news.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ashita.news.R
import com.ashita.news.ui.adapter.NewsRowItemAdapter
import com.ashita.news.utils.EspressoIdleResource
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ArticleOpenFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdleResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdleResource.countingIdlingResource)
    }

    @Test
    fun should_verify_clicked_item_on_category_row_item() {
        onView(withId(R.id.rvNews)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.rvNews))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<NewsRowItemAdapter.NewsRowItemViewHolder>(
                    0,
                    click()
                )
            );
        onView(withId(R.id.newsTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.newsTitle)).check(matches(not(withText(""))));
    }

}