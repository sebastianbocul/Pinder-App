package com.pinder.login.util

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.pinder.app.R
import com.pinder.app.StartActivity
import org.hamcrest.Matchers

fun loginEmail() {
    val testUser = "test@test.test"
    val testUserPassword = "dupa12"
    //CHECK LOGIN VIEW
    Thread.sleep(1000);
//    Espresso.onView(ViewMatchers.withId(R.id.continue_email)).perform(ViewActions.click());

    Espresso.onView(ViewMatchers.withId(R.id.loginActivity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.email)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.password)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.login)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    //perform typetext and click login button
    Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.replaceText(testUser))
    Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.replaceText(testUserPassword))
    Espresso.onView(ViewMatchers.withId(R.id.login)).perform(ViewActions.click())
//        val activityScenario2 = ActivityScenario.launch(MainFragmentManager::class.java)
    Thread.sleep(5000);
}