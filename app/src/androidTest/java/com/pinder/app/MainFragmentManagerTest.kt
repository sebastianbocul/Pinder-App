package com.pinder.app
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
import com.pinder.app.util.login
import com.pinder.app.util.logout
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
    @RepeatTest(2)
    fun click_randomNavigationButtons() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        Thread.sleep(5000);
        val array = intArrayOf(R.id.nav_settings,R.id.nav_tags,R.id.nav_main,R.id.nav_matches,R.id.nav_profile)
        for (x in 0 until 3){
            val random = Random.nextInt(0, 5)
            onView(withId(array[random])).perform(click())
        }
    }
}