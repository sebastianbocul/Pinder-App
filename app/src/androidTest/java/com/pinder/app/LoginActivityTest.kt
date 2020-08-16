package com.pinder.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith




@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest{
    @Test
    fun test_login_activity() {

        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.register)).perform(click())

        onView(withId(R.id.name)).check(matches(isDisplayed()))
    }

    /**
     * Test both ways to navigate from SecondaryActivity to MainActivity
     */
//    @Test
//    fun test_backPress_toMainActivity() {
//
//        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
//
//        onView(withId(R.id.button_next_activity)).perform(click())
//
//        onView(withId(R.id.secondary)).check(matches(isDisplayed()))
//
//        onView(withId(R.id.button_back)).perform(click()) // method 1
//
//        onView(withId(R.id.main)).check(matches(isDisplayed()))
//
//        onView(withId(R.id.button_next_activity)).perform(click())
//
//        pressBack() // method 2
//
//        onView(withId(R.id.main)).check(matches(isDisplayed()))
//    }
}
