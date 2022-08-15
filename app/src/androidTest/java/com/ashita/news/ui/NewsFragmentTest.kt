package com.ashita.news.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ashita.news.R
import com.ashita.news.ui.adapter.NewsRowItemAdapter
import com.ashita.news.utils.EspressoIdleResource
import com.ashita.news.utils.ViewMatcher.atPosition
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NewsFragmentTest {

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
    fun should_verify_news_category_item_text_in_adapter() {
        onView(withId(R.id.rvCategory)).check(matches(isDisplayed()))
        onView(withId(R.id.rvCategory))
            .check(matches(atPosition(0, hasDescendant(withText("General")))))
    }

    @Test
    fun should_verify_news_category_item_text_in_adapter_which_is_not_visible() {
        onView(withId(R.id.rvCategory)).check(matches(isDisplayed()))
        onView(withId(R.id.rvCategory))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(6))
            .check(matches(atPosition(6, hasDescendant(withText("Technology")))))
    }

    @Test
    fun should_verify_click_on_news_category_adapter() {
        onView(withId(R.id.rvCategory)).check(matches(isDisplayed()))
        onView(withId(R.id.rvCategory))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            );
    }

    @Test
    fun should_verify_click_on_category_item_row_adapter() {
        onView(withId(R.id.rvNews)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.rvNews))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<NewsRowItemAdapter.NewsRowItemViewHolder>(
                    0,
                    click()
                )
            );
    }
}