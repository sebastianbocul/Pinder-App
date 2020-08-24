package com.pinder.login.util

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.pinder.app.R

fun logout() {
    Thread.sleep(2000);
    //check if settings nav button displayed
    Espresso.onView(ViewMatchers.withId(R.id.nav_settings)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.nav_settings)).perform(ViewActions.click())
    Thread.sleep(1000);

    //check signoutbutton displayed
    Espresso.onView(ViewMatchers.withId(R.id.logoutUser)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.logoutUser)).perform(ViewActions.click())
    Thread.sleep(3000);
}