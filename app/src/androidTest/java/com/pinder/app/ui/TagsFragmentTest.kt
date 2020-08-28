package com.pinder.app.ui

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.LoginActivity
import com.pinder.app.R
import com.pinder.app.adapters.TagsAdapter
import com.pinder.app.adapters.TagsManagerAdapter
import com.pinder.app.util.RecyclerViewSizeMatcher
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

    @get:Rule
    var activityScenarioRule = activityScenarioRule<LoginActivity>()

    @Test
    @RepeatTest(1)
    fun isTagEmpty_addTag_changeTagNameGender_checkIfDataChanged_delTag() {
        Thread.sleep(5000);
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
        for (x in 0 until 10) {
            addTag_changeTagNameGender_checkIfDataChanged_delTag()
        }
    }

    private fun addTag_changeTagNameGender_checkIfDataChanged_delTag() {
        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())

        //go tags
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        Thread.sleep(500)

        //check if empty
        onView(withId(R.id.tagsRecyclerView)).check(matches(RecyclerViewSizeMatcher.recyclerViewSizeMatcher(0)))
        //add default tag
        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())

        onView(withId(R.id.tagsEditText)).perform(click())
        Thread.sleep(1000)
        val tagName = "default"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName))
        Thread.sleep(1000)
        onView(withId(R.id.radioButtonAny)).perform(click())
        //set tagname and gender
        Thread.sleep(1000)
        onView(withText("ADD")).perform(click());

        onView(withText("#default")).check(matches(isDisplayed()))
        onView(withText("Any")).check(matches(isDisplayed()))

        //edittag
        Thread.sleep(1000)
        onView(withText("#default")).perform(click());
        onView(withId(R.id.tagsEditText)).perform(click())
        onView(withId(R.id.tagsEditText)).perform(clearText())
        onView(withId(R.id.tagsEditText)).perform(typeText("date"))
        onView(withId(R.id.radioButtonFemale)).perform(click())
        onView(withText("edit")).perform(click());

        //check if exist
        onView(withText("#date")).check(matches(isDisplayed()))
        onView(withText("Female")).check(matches(isDisplayed()))
        onView(withText("#default")).check(doesNotExist())

        //edittag
        Thread.sleep(1000)
        onView(withText("#date")).perform(click());
        onView(withId(R.id.tagsEditText)).perform(click())
        onView(withId(R.id.tagsEditText)).perform(clearText())
        onView(withId(R.id.tagsEditText)).perform(typeText("chat"))
        onView(withId(R.id.radioButtonMale)).perform(click())
        onView(withText("edit")).perform(click());
        Thread.sleep(1000)
        //check if exist
        onView(withText("#chat")).check(matches(isDisplayed()))
        onView(withText("Male")).check(matches(isDisplayed()))
        onView(withText("#default")).check(doesNotExist())
        onView(withText("#date")).check(doesNotExist())

        Thread.sleep(500)
        clickOnImageViewAtRow(0)
        Thread.sleep(500)
        onView(withText("#chat")).check(doesNotExist())
        Thread.sleep(500)
    }


    //swipe to delete testing
    @Test
    @RepeatTest(1)
    fun isTagEmpty_addTag_swipeLeftToDeleteTag() {
        Thread.sleep(5000);
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
        for (x in 0 until 1) {
            //swiping doesnt work on item in recyclerview item? ???
            //you can click it but not swipe???
          //  TODO()
         //  fun_isTagEmpty_addTag_swipeLeftToDeleteTag()
        }
    }

    private fun fun_isTagEmpty_addTag_swipeLeftToDeleteTag() {
        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())

        //go tags
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        Thread.sleep(500)

        Thread.sleep(2000)
////        onView(withText("#default"))..perform(swipeLeftt());
////        onView(withId(R.id.tagsRecyclerViewItem)).perform(swipeLeftt());
////
//////        onView(withChild(withText("#default"))).perform(swipeLeftt());
////
////        Thread.sleep(2000)
////        onView(withText("#date")).perform(swipeLeftt());
////        Thread.sleep(2000)
////        onView(withText("#chat")).perform(swipeLeftt());
////        Thread.sleep(2000)
////        onView(withChild(withText("#default"))).perform(swipeLeftt());
////        Thread.sleep(2000)
////        onView(withId(R.id.tagsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<TagsAdapter.TagsViewHolder>(0, GeneralSwipeAction(
//////                Swipe.SLOW, GeneralLocation.BOTTOM_RIGHT, GeneralLocation.BOTTOM_LEFT,
//////                Press.FINGER)));
////
//        Thread.sleep(2000)
//        onView(withId(R.id.tagsRecyclerView)).perform(RecyclerViewActions.actionOnItem<TagsAdapter.TagsViewHolder>(hasDescendant(withText("#chat")), swipeLeft()));
//
//        Thread.sleep(2000)
////        onView(withId(R.id.tagsRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<TagsAdapter.TagsViewHolder>(2, click()));
//        Thread.sleep(2000)


        //check if empty
        onView(withId(R.id.tagsRecyclerView)).check(matches(RecyclerViewSizeMatcher.recyclerViewSizeMatcher(0)))
        //add default tag
        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())

        onView(withId(R.id.tagsEditText)).perform(click())
        Thread.sleep(1000)
        val tagName = "default"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName))
        Thread.sleep(1000)
        onView(withId(R.id.radioButtonAny)).perform(click())
        //set tagname and gender
        Thread.sleep(1000)
        onView(withText("ADD")).perform(click());

        //add date tag
        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())
        //set tagname and gender
        onView(withId(R.id.tagsEditText)).perform(click())
        val tagName2 = "date"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName2))
        onView(withId(R.id.radioButtonAny)).perform(click())
        onView(withText("ADD")).perform(click());

        //add date tag
        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())
        //set tagname and gender
        onView(withId(R.id.tagsEditText)).perform(click())
        val tagName3 = "chat"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName3))
        onView(withId(R.id.radioButtonAny)).perform(click())
        onView(withText("ADD")).perform(click());
        Thread.sleep(1000)


        //check if exist
        onView(withText("#default")).check(matches(isDisplayed()))
        onView(withText("#date")).check(matches(isDisplayed()))
        onView(withText("#chat")).check(matches(isDisplayed()))


        //go tags
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        Thread.sleep(500)
        Thread.sleep(1000)
//        delete
        Thread.sleep(2000)
        onView(withText("#default")).perform(swipeLeftt());
        Thread.sleep(2000)
        onView(withText("#date")).perform(swipeLeftt());
        Thread.sleep(2000)
        onView(withText("#chat")).perform(swipeLeftt());

        // this dont work
        onView(withId(R.id.tagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, swipeLeftt()));
        onView(withId(R.id.tagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, swipeLeftt()));
        onView(withId(R.id.tagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, swipeLeftt()));
        Thread.sleep(500)
        //check if exist
        onView(withText("#default")).check(doesNotExist())
        onView(withText("#date")).check(doesNotExist())
        onView(withText("#chat")).check(doesNotExist())

        Thread.sleep(500)
        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        Thread.sleep(500)

    }

    fun swipeLeftt(): ViewAction? {
        return GeneralSwipeAction(Swipe.SLOW, GeneralLocation.CENTER_RIGHT,
                GeneralLocation.CENTER_LEFT, Press.FINGER)
    }

    fun swipeLeftt222(): ViewAction? {
        return GeneralSwipeAction(Swipe.SLOW, CoordinatesProvider { view ->
            val coordinates = GeneralLocation.CENTER_RIGHT.calculateCoordinates(view)
            coordinates[0] += 200f
            Log.d("TestTag", "swipeLeftt: ${coordinates[0]}  --- ${coordinates[1]}")
            coordinates
        }, GeneralLocation.CENTER_LEFT, Press.FINGER)
    }

    //starts logged in/ empty tags list
    //check tags recyclerview on top(shoudnt exist)
    //check if card exist by clicking on it(shoudnt exist)
    //go tags, add tag
    //check tags recyclerview on top(should exist)
    //go mainfragment check if cards exists(should exist)
    //open card, go back
    //go tags and delete tags
    //go back mainfragment
    //repeat
    @Test
    fun isCardsEmpty_addTag_isCardsNotEmpty_removeCard_isCardsEmpty() {
        Thread.sleep(5000);
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
        for (x in 0 until 3) {
            isCardsEmpty_addTag_isCardsNotEmpty_removeCard_isCardsEmptyy()
        }
    }

    fun isCardsEmpty_addTag_isCardsNotEmpty_removeCard_isCardsEmptyy() {
        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        //check cards not exists
        onView(withId(R.id.noMore)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        //checking if frame is empty
        onView(withId(R.id.item_card)).check(doesNotExist());
        Thread.sleep(1000)
        //checking if tags are empty
        onView(withId(R.id.item_tags)).check(doesNotExist());
        onView(withText(R.string.emptyTags)).check(matches(isDisplayed()))
        //go tags
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        Thread.sleep(500)

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

        onView(withId(R.id.tagsEditText)).perform(click())
        Thread.sleep(1000)
        val tagName = "default"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName))
        Thread.sleep(1000)
        onView(withId(R.id.radioButtonAny)).perform(click())
        //set tagname and gender
        Thread.sleep(1000)
        onView(withText("ADD")).perform(click());

        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())
        //set tagname and gender
        onView(withId(R.id.tagsEditText)).perform(click())
        val tagName2 = "date"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName2))
        onView(withId(R.id.radioButtonAny)).perform(click())
        onView(withText("ADD")).perform(click());

        Thread.sleep(3000)

        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        onView(withText(R.string.loadingUsers)).check(matches(isDisplayed()))
        Thread.sleep(5000)
        //this is recyclerView in main fragment
        onView(withId(R.id.mainTagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, click()))
        //click frame and if
        onView(withId(R.id.frame)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.userProfileActivity)).check(matches(isDisplayed()))

        pressBack()
        Thread.sleep(1000)
        //check if tags are added
        onView(withId(R.id.mainTagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.mainTagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(1, click()))

        //go tags
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        Thread.sleep(500)
        clickOnImageViewAtRow(1)
        Thread.sleep(500)
        clickOnImageViewAtRow(0)

        //go mainfragment
        Thread.sleep(1000)
        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        onView(withText(R.string.emptyTags)).check(matches(isDisplayed()))
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }


    //similar to isCardsEmpty_addTag_isCardsNotEmpty_removeCard_isCardsEmpty_logout
    //but also checks edit tags
    @Test
    fun is_tagsEmpty_editTag_checkIfMatch_goMainCheckIfChanged_delTags_checkIfDeleted() {
        Thread.sleep(5000);
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
        for (x in 0 until 3) {
            goToTags_editTag_checkIfMatch_goMainCheckIfChanged()
        }
    }

    private fun goToTags_editTag_checkIfMatch_goMainCheckIfChanged() {
        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        //check cards not exists
        onView(withId(R.id.noMore)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        //checking if frame is empty
        onView(withId(R.id.item_card)).check(doesNotExist());
        Thread.sleep(1000)
        //checking if tags are empty
        onView(withId(R.id.item_tags)).check(doesNotExist());
        onView(withText(R.string.emptyTags)).check(matches(isDisplayed()))
        //go tags
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        Thread.sleep(500)

        //add default tag
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

        onView(withId(R.id.tagsEditText)).perform(click())
        Thread.sleep(1000)
        val tagName = "default"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName))
        Thread.sleep(1000)
        onView(withId(R.id.radioButtonAny)).perform(click())
        //set tagname and gender
        Thread.sleep(1000)
        onView(withText("ADD")).perform(click());

        //add date tag
        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())
        //set tagname and gender
        onView(withId(R.id.tagsEditText)).perform(click())
        val tagName2 = "date"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName2))
        onView(withId(R.id.radioButtonAny)).perform(click())
        onView(withText("ADD")).perform(click());

        //add date tag
        onView(withId(R.id.button_add_tag)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_tag)).perform(click())
        //set tagname and gender
        onView(withId(R.id.tagsEditText)).perform(click())
        val tagName3 = "chat"
        onView(withId(R.id.tagsEditText)).perform(typeText(tagName3))
        onView(withId(R.id.radioButtonAny)).perform(click())
        onView(withText("ADD")).perform(click());

        Thread.sleep(1000)
        onView(withText("#default")).perform(click());
        onView(withId(R.id.tagsEditText)).perform(click())
        onView(withId(R.id.tagsEditText)).perform(typeText("2222312"))
        onView(withId(R.id.radioButtonFemale)).perform(click())
        onView(withText("edit")).perform(click());

        onView(withText("#date")).perform(click());
        onView(withId(R.id.tagsEditText)).perform(click())
        onView(withId(R.id.tagsEditText)).perform(typeText("222132"))
        onView(withId(R.id.radioButtonMale)).perform(click())
        onView(withText("edit")).perform(click());

        onView(withText("#chat")).perform(click());
        onView(withId(R.id.tagsEditText)).perform(click())
        onView(withId(R.id.tagsEditText)).perform(typeText("121332"))
        onView(withId(R.id.radioButtonAny)).perform(click())
        onView(withText("edit")).perform(click());
        onView(withText("#default2222312")).check(matches(isDisplayed()))
        onView(withText("#date222132")).check(matches(isDisplayed()))
        onView(withText("#chat121332")).check(matches(isDisplayed()))

        onView(withText("#default")).check(doesNotExist())
        onView(withText("#date")).check(doesNotExist())
        onView(withText("#chat")).check(doesNotExist())

        Thread.sleep(3000)

        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        Thread.sleep(5000)
        onView(withText(R.string.noMoreUsers)).check(matches(isDisplayed()))
        onView(withText("#default2222312")).check(matches(isDisplayed()))
        onView(withText("#date222132")).check(matches(isDisplayed()))
        onView(withText("#chat121332")).check(matches(isDisplayed()))

        //this is recyclerView in main fragment
        onView(withId(R.id.mainTagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, click()))
        //click frame and if
//        onView(withId(R.id.frame)).perform(click())
        Thread.sleep(1000)
//        onView(withId(R.id.userProfileActivity)).check(matches(isDisplayed()))

//        pressBack()
        Thread.sleep(1000)
        //check if tags are added
        onView(withId(R.id.mainTagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.mainTagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.mainTagsRecyclerView)).perform(actionOnItemAtPosition<TagsManagerAdapter.ViewHolder>(0, click()))

        //go tags
        onView(withId(R.id.nav_tags)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_tags)).perform(click())
        Thread.sleep(500)
        clickOnImageViewAtRow(0)
        Thread.sleep(500)
        clickOnImageViewAtRow(0)
        clickOnImageViewAtRow(0)

        //go mainfragment
        Thread.sleep(1000)
        onView(withId(R.id.nav_main)).check(matches(isDisplayed()))
        onView(withId(R.id.nav_main)).perform(click())
        onView(withId(R.id.mainFragmentManager)).check(matches(isDisplayed()))
        Thread.sleep(3000)

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