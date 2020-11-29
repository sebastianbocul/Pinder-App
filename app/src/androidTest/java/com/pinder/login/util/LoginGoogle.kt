package com.pinder.login.util

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.pinder.app.R
import com.pinder.app.StartActivity
import org.hamcrest.Matchers

fun loginGoogle() {
    //CHECK LOGIN VIEW
    Thread.sleep(1000);
    Espresso.onView(ViewMatchers.withId(R.id.continue_google)).perform(ViewActions.click());
//        val activityScenario2 = ActivityScenario.launch(MainFragmentManager::class.java)
    Thread.sleep(5000);
}