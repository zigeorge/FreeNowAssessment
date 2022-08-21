package com.george.freenowassessment.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.MediumTest
import com.george.freenowassessment.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@MediumTest
@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun checkNavigationBetweenFragments() = runTest {
        assertVehiclesFragment()

        openMapFragment()

        assertMapFragment()

        openVehiclesFragment()
    }

    @Test
    fun checkNavigationToMapAndBackPress() = runTest {

        assertVehiclesFragment()

        openMapFragment()

        assertMapFragment()

        pressBack()

        assertVehiclesFragment()

    }

    @Test
    fun checkVehiclesPageScrollable() = runTest {

        assertVehiclesFragment()

        delay(5000)

        onView(allOf(withContentDescription(R.string.cd_vehicle_list),
            isDisplayed())).perform(swipeUp())

        assertVehiclesFragment()

    }

    private fun assertVehiclesFragment() {
        onView(withId(R.id.vehiclesFragment))
            .check(matches(isDisplayed()))
    }

    private fun openVehiclesFragment() {
        onView(allOf(withContentDescription(R.string.title_list), isDisplayed()))
            .perform(click())
    }

    private fun assertMapFragment() {
        onView(withId(R.id.mapFragment))
            .check(matches(isDisplayed()))
    }

    private fun openMapFragment() {
        onView(allOf(withContentDescription(R.string.title_map), isDisplayed()))
            .perform(click())
    }

}