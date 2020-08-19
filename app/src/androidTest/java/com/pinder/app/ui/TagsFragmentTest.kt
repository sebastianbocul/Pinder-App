package com.pinder.app.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.*
import com.pinder.app.repeatutil.RepeatRule
import com.pinder.app.repeatutil.RepeatTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)

class TagsFragmentTest {
    @Rule
    @JvmField
    var repeatRule: RepeatRule = RepeatRule()

    @Test
    @RepeatTest(1)
    fun login_clickNaviagtionButtons_logout() {

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

        //go to Tags fragment
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        //perform clicking navigationBars
        logout()
    }


    fun logout() {
        Thread.sleep(2000);

        //check if settings nav button displayed
        onView(withId(R.id.nav_settings)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_settings)).perform(click())
        Thread.sleep(1000);

        //check signoutbutton displayed
        onView(withId(R.id.logoutUser)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutUser)).perform(click())
        Thread.sleep(3000);
    }




}