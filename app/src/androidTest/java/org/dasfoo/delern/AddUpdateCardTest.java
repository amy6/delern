package org.dasfoo.delern;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.InputType;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.core.AllOf.allOf;


/**
 * Tests creation and updating a card.
 */
@RunWith(AndroidJUnit4.class)
public class AddUpdateCardTest {
    @Rule
    public ActivityTestRule<DelernMainActivity> mActivityRule = new ActivityTestRule<>(
            DelernMainActivity.class);

    @Rule
    public FirebaseOperationInProgressRule mFirebaseRule = new FirebaseOperationInProgressRule();

    private final String mDeckName = "TestAddUpdate";

    @Before
    public void createDeck() {
        waitView(withId(R.id.fab)).perform(click());
        waitView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(mDeckName), closeSoftKeyboard());
        waitView(withText(R.string.add)).perform(click());
    }

    @Test
    public void createCard() {
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        waitView(withId(R.id.front_side_text)).perform(typeText("front"));
        waitView(withId(R.id.back_side_text)).perform(typeText("back"), closeSoftKeyboard());
        waitView(withId(R.id.add_card_to_db)).perform(click());
        //Check that fields are empty after adding card
        waitView(withId(R.id.front_side_text)).check(matches(withText("")));
        waitView(withId(R.id.back_side_text)).check(matches(withText("")));
        pressBack();
        // Check that deck with 1 card was created
        waitView(withText(mDeckName)).check(matches(hasSibling(withText("1"))));
    }

    @Test
    public void createReversedCard() {
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        waitView(withId(R.id.front_side_text)).perform(typeText("front"));
        waitView(withId(R.id.back_side_text)).perform(typeText("back"), closeSoftKeyboard());
        waitView(withId(R.id.add_reversed_card_checkbox)).perform(click());
        waitView(withId(R.id.add_card_to_db)).perform(click());
        pressBack();
        // Check that deck with 2 card was created
        waitView(withText(mDeckName)).check(matches(hasSibling(withText("2"))));
    }

    @Test
    public void createCardToUpdateFromPreview() {
        String frontCard = "front";
        String backCard = "back";
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        waitView(withId(R.id.front_side_text)).perform(typeText(frontCard));
        waitView(withId(R.id.back_side_text)).perform(typeText(backCard), closeSoftKeyboard());
        waitView(withId(R.id.add_card_to_db)).perform(click());
        pressBack();
        waitView(withText(mDeckName)).check(matches(hasSibling(withText("1"))));
        waitView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        waitView(withText(R.string.edit)).perform(click());
        waitView(allOf(withText(frontCard), hasSibling(withText(backCard)))).perform(click());
        waitView(withId(R.id.textFrontPreview)).check(matches(withText(frontCard)));
        waitView(withId(R.id.textBackPreview)).check(matches(withText(backCard)));
        waitView(withId(R.id.edit_card_button)).check(matches(isDisplayed())).perform(click());
        waitView(withId(R.id.front_side_text)).check(matches(withText(frontCard)))
                .perform(replaceText("front2"));
        waitView(withId(R.id.back_side_text)).check(matches(withText(backCard)))
                .perform(replaceText("back2"));
        waitView(withId(R.id.add_card_to_db)).perform(click());
        waitView(withId(R.id.textFrontPreview)).check(matches(withText("front2")));
        waitView(withId(R.id.textBackPreview)).check(matches(withText("back2")));
        pressBack();
        pressBack();
    }

    @Test
    public void createCardToUpdateFromLearningShowingFront() {
        String frontCard = "front";
        String backCard = "back";
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        waitView(withId(R.id.front_side_text)).perform(typeText(frontCard));
        waitView(withId(R.id.back_side_text)).perform(typeText(backCard), closeSoftKeyboard());
        waitView(withId(R.id.add_card_to_db)).perform(click());
        pressBack();
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("1"))))
                .perform(click());
        // Check that front side is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(frontCard)));

        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        waitView(withText(R.string.edit)).perform(click());
        waitView(withId(R.id.front_side_text)).check(matches(withText(frontCard)))
                .perform(replaceText("front2"));
        waitView(withId(R.id.add_card_to_db)).perform(click());
        // Check that front side in Learning Activity is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText("front2")));
        pressBack();
    }

    @Test
    public void createCardToUpdateFromLearningShowingBack() {
        String frontCard = "front";
        String backCard = "back";
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        waitView(withId(R.id.front_side_text)).perform(typeText(frontCard));
        waitView(withId(R.id.back_side_text)).perform(typeText(backCard), closeSoftKeyboard());
        waitView(withId(R.id.add_card_to_db)).perform(click());
        pressBack();
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("1"))))
                .perform(click());
        // Check that front side is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(frontCard)));
        // Flip card
        waitView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        waitView(withId(R.id.textBackCardView)).check(matches(withText(backCard)));
        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        waitView(withText(R.string.edit)).perform(click());

        waitView(withId(R.id.front_side_text)).check(matches(withText(frontCard)))
                .perform(replaceText("front2"));
        waitView(withId(R.id.back_side_text)).check(matches(withText(backCard)))
                .perform(replaceText("back2"));
        waitView(withId(R.id.add_card_to_db)).perform(click());
        // Check that front side in Learning Activity is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText("front2")));
        waitView(withId(R.id.textBackCardView)).check(matches(withText("back2")));
        pressBack();
    }

    @After
    public void deleteDeck() {
        waitView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        waitView(withText(R.string.delete)).perform(click());
        waitView(withText(R.string.delete)).perform(click());
    }
}
