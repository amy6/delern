/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.InputType;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.dasfoo.delern.util.DeckPostfix;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
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
    public TestName mName = new TestName();

    @Rule
    public FirebaseOperationInProgressRule mFirebaseRule = new FirebaseOperationInProgressRule();

    private String mDeckName;

    @Before
    public void createDeck() {
        mDeckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(withId(R.id.fab)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(mDeckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
    }

    @Test
    public void createCard() {
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        onView(withId(R.id.front_side_text)).perform(typeText("front"));
        onView(withId(R.id.back_side_text)).perform(typeText("back"), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        // Check that fields are empty after adding card
        waitView(withId(R.id.front_side_text)).check(matches(withText("")));
        onView(withId(R.id.back_side_text)).check(matches(withText("")));
        pressBack();
        // Check that deck with 1 card was created
        waitView(withText(mDeckName)).check(matches(hasSibling(withText("1"))));
    }

    @Test
    public void createReversedCard() {
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        onView(withId(R.id.front_side_text)).perform(typeText("front"));
        onView(withId(R.id.back_side_text)).perform(typeText("back"), closeSoftKeyboard());
        onView(withId(R.id.add_reversed_card_checkbox)).perform(click());
        onView(withId(R.id.add_card_to_db)).perform(click());
        // Check that fields are empty after adding card
        waitView(withId(R.id.front_side_text)).check(matches(withText("")));
        onView(withId(R.id.back_side_text)).check(matches(withText("")));
        pressBack();
        // Check that deck with 2 card was created
        waitView(withText(mDeckName)).check(matches(hasSibling(withText("2"))));
    }

    @Test
    public void createCardToUpdateFromPreview() {
        String frontCard = "front";
        String backCard = "back";
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        onView(withId(R.id.front_side_text)).perform(typeText(frontCard));
        onView(withId(R.id.back_side_text)).perform(typeText(backCard), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        // Check that fields are empty after adding card
        waitView(withId(R.id.front_side_text)).check(matches(withText("")));
        onView(withId(R.id.back_side_text)).check(matches(withText("")));
        pressBack();
        waitView(withText(mDeckName)).check(matches(hasSibling(withText("1"))));
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.edit_cards_deck_menu)).perform(click());
        waitView(allOf(withText(frontCard), hasSibling(withText(backCard)))).perform(click());
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(frontCard)));
        onView(withId(R.id.textBackCardView)).check(matches(withText(backCard)));
        onView(withId(R.id.edit_card_button)).check(matches(isDisplayed())).perform(click());
        waitView(withId(R.id.front_side_text)).check(matches(withText(frontCard)))
                .perform(replaceText("front2"), closeSoftKeyboard());
        onView(withId(R.id.back_side_text)).check(matches(withText(backCard)))
                .perform(replaceText("back2"), closeSoftKeyboard());
        pressBack();
        waitView(withId(R.id.textFrontCardView)).check(matches(withText("front2")));
        onView(withId(R.id.textBackCardView)).check(matches(withText("back2")));
        pressBack();
        pressBack();
    }

    @Test
    public void createCardFromCardsList() {
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        pressBack();
        pressBack();
        waitView(withText(mDeckName)).check(matches(hasSibling(withText("0"))));
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.edit_cards_deck_menu)).perform(click());
        waitView(withId(R.id.f_add_card_button)).perform(click());
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        String frontCard = "front";
        String backCard = "back";
        onView(withId(R.id.front_side_text)).perform(typeText(frontCard));
        onView(withId(R.id.back_side_text)).perform(typeText(backCard), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        // Check that fields are empty after adding card
        waitView(withId(R.id.front_side_text)).check(matches(withText("")));
        onView(withId(R.id.back_side_text)).check(matches(withText("")));
        pressBack();
        waitView(withText(frontCard)).check(matches(hasSibling(withText(backCard))));
        pressBack();
    }


    @Test
    public void createCardToUpdateFromLearningShowingFront() {
        String frontCard = "front";
        String backCard = "back";
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        onView(withId(R.id.front_side_text)).perform(typeText(frontCard));
        onView(withId(R.id.back_side_text)).perform(typeText(backCard), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        // Check that fields are empty after adding card
        waitView(withId(R.id.front_side_text)).check(matches(withText("")));
        onView(withId(R.id.back_side_text)).check(matches(withText("")));
        pressBack();
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("1"))))
                .perform(click());
        // Check that front side is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(frontCard)));

        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.edit)).perform(click());
        waitView(withId(R.id.front_side_text)).check(matches(withText(frontCard)))
                .perform(replaceText("front2"), closeSoftKeyboard());
        pressBack();
        // Check that front side in Learning Activity is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText("front2")));
        pressBack();
    }

    @Test
    public void createCardToUpdateFromLearningShowingBack() {
        String frontCard = "front";
        String backCard = "back";
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        onView(withId(R.id.front_side_text)).perform(typeText(frontCard));
        onView(withId(R.id.back_side_text)).perform(typeText(backCard), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        pressBack();
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("1"))))
                .perform(click());
        // Check that front side is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(frontCard)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(backCard)));
        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.edit)).perform(click());

        waitView(withId(R.id.front_side_text)).check(matches(withText(frontCard)))
                .perform(replaceText("front2"), closeSoftKeyboard());
        onView(withId(R.id.back_side_text)).check(matches(withText(backCard)))
                .perform(replaceText("back2"), closeSoftKeyboard());
        pressBack();
        // Check that front side in Learning Activity is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText("front2")));
        onView(withId(R.id.textBackCardView)).check(matches(withText("back2")));
        pressBack();
    }

    @After
    public void deleteDeck() {
        waitView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.deck_settings_menu)).perform(click());
        waitView(withId(R.id.delete_deck_menu)).perform(click());
        onView(withText(R.string.delete)).perform(click());
    }
}
