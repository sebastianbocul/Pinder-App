package com.pinder.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.repeatutil.RepeatRule
import com.pinder.app.repeatutil.RepeatTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class NaviagionTest {
    @Rule
    @JvmField
    var repeatRule: RepeatRule = RepeatRule()

    fun if_loggedInAlready_goSignOut() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        Thread.sleep(3000);
        //check if settings nav button displayed
        onView(withId(R.id.nav_settings)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_settings)).perform(click())

        //check signoutbutton displayed
        onView(withId(R.id.logoutUser)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutUser)).perform(click())
        Thread.sleep(3000);
    }

    fun test_logIn() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        val testUser = "test@test.test"
        val testUserPassword = "dupa12"
        //CHECK LOGIN VIEW
        Thread.sleep(1000);
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.email)).check(matches(isDisplayed()))
        onView(withId(R.id.password)).check(matches(isDisplayed()))
        onView(withId(R.id.login)).check(matches(isDisplayed()))

        //perform typetext and click login button
        onView(withId(R.id.email)).perform(typeText(testUser))
        onView(withId(R.id.password)).perform(typeText(testUserPassword))
        onView(withId(R.id.login)).perform(click())
//        val activityScenario2 = ActivityScenario.launch(MainFragmentManager::class.java)

        Thread.sleep(5000);
        //check main activity
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
    }

    @Test
    @RepeatTest(1)
    fun test_login_goSettings_logout() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        val testUser = "test@test.test"
        val testUserPassword = "dupa12"
        //CHECK LOGIN VIEW
        Thread.sleep(1000);
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.email)).check(matches(isDisplayed()))
        onView(withId(R.id.password)).check(matches(isDisplayed()))
        onView(withId(R.id.login)).check(matches(isDisplayed()))

        //perform typetext and click login button
        onView(withId(R.id.email)).perform(typeText(testUser))
        onView(withId(R.id.password)).perform(typeText(testUserPassword))
        onView(withId(R.id.login)).perform(click())
//        val activityScenario2 = ActivityScenario.launch(MainFragmentManager::class.java)

        Thread.sleep(8000);
        //check main activity
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))

        Thread.sleep(2000);
//        onView(withId(R.id.ageRangeSeeker)).check(matches(isDisplayed()))

        //check if settings nav button displayed
        onView(withId(R.id.nav_settings)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_settings)).perform(click())
        Thread.sleep(0);

        //check signoutbutton displayed
        onView(withId(R.id.logoutUser)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutUser)).perform(click())
        Thread.sleep(3000);
    }
}