package com.pinder.login

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.R
import com.pinder.app.StartActivity
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
import com.pinder.login.util.loginEmail
import com.pinder.login.util.loginGoogle
import com.pinder.login.util.logout
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest {
    @Rule
    @JvmField
    var repeatRule: RepeatRule = RepeatRule()


    @Test
    fun test_isActivityInView() {
        val activityScenario = ActivityScenario.launch(StartActivity::class.java)
        logout()
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
        loginEmail()
    }

    @Test
    fun test_elements_visibility() {
        logout()
        val activityScenario = ActivityScenario.launch(StartActivity::class.java)
        onView(withId(R.id.regulationsTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.logo_text_view)).check(matches(isDisplayed()))
        onView(withId(R.id.continue_email)).check(matches(isDisplayed()))
        onView(withId(R.id.continue_facebook)).check(matches(isDisplayed()))
        onView(withId(R.id.continue_google)).check(matches(isDisplayed()))
        onView(withId(R.id.continue_with_phone)).check(matches(isDisplayed()))
        onView(withId(R.id.smallLogo)).check(matches(isDisplayed()))
        loginEmail()
    }

    @Test
    @RepeatTest(5)
    fun test_loginEmail_goSettings_logout() {
        logout()
        loginEmail()
    }

    @Test
    @RepeatTest(5)
    fun test_loginGoogle_goSettings_logout() {
        logout()
        loginGoogle()
    }
}
