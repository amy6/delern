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
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class DeckOperationsTest {

    @Rule
    public ActivityTestRule<DelernMainActivity> mActivityRule = new ActivityTestRule<>(
            DelernMainActivity.class);

    @Rule
    public FirebaseOperationInProgressRule mFirebaseRule = new FirebaseOperationInProgressRule();

    @Rule
    public TestName mName = new TestName();

    private static void deleteDeck(final String deckName) {
        waitView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click());
        onView(withText(R.string.deck_settings_menu)).perform(click());
        waitView(withId(R.id.delete_deck_menu)).perform(click());
        onView(withText(R.string.delete)).perform(click());
    }

    @Test
    public void noDecksMessageShown() {
        waitView(withId(R.id.fab)).check(matches(isDisplayed()));
        waitView(allOf(withId(R.id.empty_recyclerview_message),
                withText(R.string.empty_decks_message))).check(matches(isDisplayed()));
    }

    @Test
    public void addEmptyDeckAndDelete() {
        String deckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(withId(R.id.fab)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(deckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        onView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()))
                .perform(closeSoftKeyboard());
        pressBack();
        waitView(withId(R.id.fab)).check(matches(isDisplayed()));
        // Check that deck was created with 0 cards
        waitView(withText(deckName)).check(matches(hasSibling(withText("0")))).perform(click());
        // No cards to learn toast.
        onView(withText(R.string.no_card_message))
                .inRoot(withDecorView(CoreMatchers.not(mActivityRule.getActivity().getWindow()
                        .getDecorView()))).check(matches(isDisplayed()));
        onView(withId(R.id.empty_recyclerview_message)).check(matches(not(isDisplayed())));
        deleteDeck(deckName);
    }

    @Test
    public void createDeckWithCardToLearnAndDelete() {
        String deckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(withId(R.id.fab)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(deckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        onView(withId(R.id.front_side_text)).perform(typeText("front"));
        onView(withId(R.id.back_side_text)).perform(typeText("back"), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        pressBack();
        // Start Learning Activity
        waitView(allOf(withText(deckName), hasSibling(withText("1"))))
                .perform(click());
        // Check that front side is correct
        waitView(withId(R.id.textFrontCardView)).check(matches(withText("front")));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText("back")));
        pressBack();
        deleteDeck(deckName);
    }

    @Test
    public void createDeckToRenameAndDelete() {
        String deckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(withId(R.id.fab)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(deckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        waitView(withId(R.id.add_card_to_db))
                .check(matches(isDisplayed()))
                .perform(closeSoftKeyboard());
        pressBack();
        waitView(withId(R.id.fab)).check(matches(isDisplayed()));
        waitView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click());
        String newDeckName = "Rename" + DeckPostfix.getRandomNumber();
        onView(withText(R.string.rename)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(replaceText(newDeckName), closeSoftKeyboard());
        onView(withText(R.string.rename)).perform(click());
        waitView(withText(newDeckName)).check(matches(hasSibling(withText("0"))));
        deleteDeck(newDeckName);
    }

    @Test
    public void addDeckToChangeDeckTypeToGermanAndDelete() {
        String deckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(withId(R.id.fab)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(deckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        onView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.front_side_text)).perform(typeText("mother"));
        onView(withId(R.id.back_side_text)).perform(typeText("die Mutter"), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        pressBack();
        waitView(withId(R.id.fab)).check(matches(isDisplayed()));
        // Check that deck was created with 1 cards
        waitView(withText(deckName)).check(matches(hasSibling(withText("1"))));
        onView(withId(R.id.empty_recyclerview_message)).check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click());
        onView(withText(R.string.cards_type)).perform(click());
        onView(withText(R.string.basic_cards_type)).check(matches(not(isClickable())));
        onView(withText(R.string.german_cards_type)).perform(click());
        // Check that deck type was changed
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click());
        onView(withText(R.string.cards_type)).perform(click());
        onView(withText(R.string.german_cards_type)).check(matches(not(isClickable())));
        pressBack();
        deleteDeck(deckName);
    }

    @Test
    public void addDeckToChangeDeckTypeToSwissAndDelete() {
        String deckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(withId(R.id.fab)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(deckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        onView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.front_side_text)).perform(typeText("mother"));
        onView(withId(R.id.back_side_text)).perform(typeText("d Mutter"), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        pressBack();
        waitView(withId(R.id.fab)).check(matches(isDisplayed()));
        // Check that deck was created with 1 cards
        waitView(withText(deckName)).check(matches(hasSibling(withText("1"))));
        onView(withId(R.id.empty_recyclerview_message)).check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click());
        onView(withText(R.string.cards_type)).perform(click());
        onView(withText(R.string.basic_cards_type)).check(matches(not(isClickable())));
        onView(withText(R.string.swissgerman_cards_type)).perform(click());
        // Check that deck type was changed
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click());
        onView(withText(R.string.cards_type)).perform(click());
        onView(withText(R.string.swissgerman_cards_type)).check(matches(not(isClickable())));
        pressBack();
        deleteDeck(deckName);
    }
}
