package com.pinder.app.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.LoginActivity
import com.pinder.app.R
import com.pinder.app.ui.TagsFragmentTest.ViewMatchers.onRecyclerItemView
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class TagsFragmentTest {
    @Rule
    @JvmField
    var repeatRule: RepeatRule = RepeatRule()

    @Test
    @RepeatTest(2)
    fun login_clickNaviagtionButtons_logout() {
        //check main activity
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        Thread.sleep(5000);
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
        //go to Tags fragment
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        //perform clicking navigationBars
    }

    @Test
    fun isCardsEmpty_addTag_isCardsNotEmpty_removeCard_isCardsEmpty_logout() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        Thread.sleep(5000);
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
        isCardsEmpty_addTag_isCardsNotEmpty_removeCard_isCardsEmpty()
    }

    fun isCardsEmpty_addTag_isCardsNotEmpty_removeCard_isCardsEmpty() {
        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        //check cards not exists
        onView(withId(R.id.noMore)).check(matches(isDisplayed()))

        //go tags
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())

        clickOnImageViewAtRow(1)
        Thread.sleep(1000)
        clickOnImageViewAtRow(0)


        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())

        //check dialog_add_tag items visibility
        onView(withId(R.id.dialog_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.tagsEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.lookingForTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.radioGroup)).check(matches(isDisplayed()))
        onView(withId(R.id.radioButtonMale)).check(matches(isDisplayed()))
        onView(withId(R.id.radioButtonFemale)).check(matches(isDisplayed()))
        onView(withId(R.id.radioButtonAny)).check(matches(isDisplayed()))
        onView(withId(R.id.ageRangeTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.ageRangeSeeker)).check(matches(isDisplayed()))
        onView(withId(R.id.maxDistanceTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.distanceSeeker)).check(matches(isDisplayed()))


        //set tagname and gender
        onView(withId(R.id.tagsEditText)).perform(click())
        val tagName = "default"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName))
        onView(withId(R.id.radioButtonAny)).perform(click())

        onView(withText("SAVE")).perform(click());

        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())
        //set tagname and gender
        onView(withId(R.id.tagsEditText)).perform(click())
        val tagName2 = "date"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName2))
        onView(withId(R.id.radioButtonAny)).perform(click())
        onView(withText("SAVE")).perform(click());

        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        Thread.sleep(5000)

    }

    fun clickOnImageViewAtRow(position: Int) {
        onView(withId(R.id.tagsRecyclerView)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(position, ClickOnImageView()))
    }

    class ClickOnImageView : ViewAction {
        var click = click()
        override fun getConstraints(): Matcher<View> {
            return click.constraints
        }

        override fun getDescription(): String {
            return " click on custom image view"
        }

        override fun perform(uiController: UiController?, view: View) {
            click.perform(uiController, view.findViewById(R.id.tag_delete))
        }
    }
}