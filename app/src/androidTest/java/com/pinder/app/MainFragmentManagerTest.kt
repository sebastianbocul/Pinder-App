package com.pinder.app
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
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
    fun click_randomNavigationButtons() {
        val activityScenario = ActivityScenario.launch(StartActivity::class.java)
        Thread.sleep(5000);
        val array = intArrayOf(R.id.nav_settings,R.id.nav_tags,R.id.nav_main,R.id.nav_matches,R.id.nav_profile)
        for (x in 0 until 50){
            val random = Random.nextInt(0, 5)
            onView(withId(array[random])).perform(click())
        }
    }
}