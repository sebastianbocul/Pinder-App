package com.pinder.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
import com.pinder.app.util.login
import com.pinder.app.util.logout
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
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_elements_visibility() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.regulationsTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.emailTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.email)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.password)).check(matches(isDisplayed()))
        onView(withId(R.id.login)).check(matches(isDisplayed()))
        onView(withId(R.id.registerTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.login_button)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()))
        onView(withId(R.id.smallLogo)).check(matches(isDisplayed()))
        onView(withId(R.id.bigLogo)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    @RepeatTest(2)
    fun test_login_goSettings_logout() {
        login()
        logout()
    }
}
