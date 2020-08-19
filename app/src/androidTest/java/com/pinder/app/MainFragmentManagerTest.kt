package com.pinder.app
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.repeatutil.RepeatRule
import com.pinder.app.repeatutil.RepeatTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random


@RunWith(AndroidJUnit4ClassRunner::class)

class MainFragmentManagerTest {
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
        onView(withId(R.id.loginActivity)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.password)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.login)).check(ViewAssertions.matches(isDisplayed()))

        //perform typetext and click login button
        onView(withId(R.id.email)).perform(typeText(testUser))
        onView(withId(R.id.password)).perform(typeText(testUserPassword))
        onView(withId(R.id.login)).perform(click())
//        val activityScenario2 = ActivityScenario.launch(MainFragmentManager::class.java)

        Thread.sleep(5000);
        //check main activity
        onView(withId(R.id.mainFragmentManager)).check(ViewAssertions.matches(isDisplayed()))

        //perform clicking navigationBars
        click_randomNavigationButtons()
        //go logout
        logout()
    }

    fun logout() {
        Thread.sleep(2000);

        //check if settings nav button displayed
        onView(withId(R.id.nav_settings)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.nav_settings)).perform(click())
        Thread.sleep(1000);

        //check signoutbutton displayed
        onView(withId(R.id.logoutUser)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.logoutUser)).perform(click())
        Thread.sleep(3000);
    }

    fun click_randomNavigationButtons() {
        val array = intArrayOf(R.id.nav_settings,R.id.nav_tags,R.id.nav_main,R.id.nav_matches,R.id.nav_profile)
        for (x in 0 until 2000){
            val random = Random.nextInt(0, 5)
            onView(withId(array[random])).perform(click())
        }
    }
}