package com.pinder.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
import com.pinder.app.util.login
import com.pinder.app.util.logout
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class NaviagionTest {
    @Rule
    @JvmField
    var repeatRule: RepeatRule = RepeatRule()
    @Test
    @RepeatTest(2)
    fun test_login_goSettings_logout() {
//        login()
//        logout()
    }
}