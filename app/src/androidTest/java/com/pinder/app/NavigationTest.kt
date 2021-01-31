package com.pinder.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
import com.pinder.login.util.loginEmail
import com.pinder.login.util.logout
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class NavigationTest {
    @Rule
    @JvmField
    var repeatRule: RepeatRule = RepeatRule()

//    @get:Rule
//    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    @RepeatTest(10)
    fun test_login_goSettings_logout() {
        logout()
        loginEmail()
    }

    @Test
    @RepeatTest(10)
    fun test_inApp_navigation() {
        val activityScenario = ActivityScenario.launch(StartActivity::class.java)
        Thread.sleep(5000)
        onView(withId(R.id.nav_tags)).perform(click())
        onView(withId(R.id.button_add_tag)).perform(click())
        pressBack()
        onView(withId(R.id.nav_matches)).perform(click())
        onView(withId(R.id.locationButton)).perform(click())
        pressBack()
        onView(withId(R.id.nav_main)).perform(click())
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.nav_settings)).perform(click())
    }

}