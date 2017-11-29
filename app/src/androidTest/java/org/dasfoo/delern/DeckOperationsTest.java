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

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.InputType;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.test.DeckPostfix;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.dasfoo.delern.test.FirebaseSignInRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.BasicOperations.deleteDeck;
import static org.dasfoo.delern.test.BasicOperations.deleteSelectedDeck;
import static org.dasfoo.delern.test.ViewMatchers.first;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class DeckOperationsTest {

    @Rule
    public ActivityTestRule<DelernMainActivity> mActivityRule = new ActivityTestRule<>(
            DelernMainActivity.class);

    @Rule
    public FirebaseOperationInProgressRule mFirebaseRule =
            new FirebaseOperationInProgressRule(true);

    @Rule
    public TestName mName = new TestName();

    @Rule
    public FirebaseSignInRule mSignInRule = new FirebaseSignInRule(true);

    @Test
    public void noDecksMessageShown() {
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));

        // Delete all existing decks.
        try {
            while (true) {
                onView(first(withId(R.id.deck_popup_menu))).perform(click());
                deleteSelectedDeck();
            }
        } catch (NoMatchingViewException e) {
            // Finished deleting all decks.
        }

        waitView(() -> onView(allOf(withId(R.id.empty_recyclerview_message),
                withText(R.string.empty_decks_message))).check(matches(isDisplayed())));
    }

    @Test
    public void createDeckToRenameAndDelete() {
        String deckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(() -> onView(withId(R.id.fab)).perform(click()));
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(deckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        waitView(() -> onView(withId(R.id.add_card_to_db))
                .check(matches(isDisplayed()))
                .perform(closeSoftKeyboard()));
        pressBack();
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        waitView(() -> onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click()));
        String newDeckName = "Rename" + DeckPostfix.getRandomNumber();
        onView(withText(R.string.deck_settings_menu)).perform(click());

        waitView(() -> onView(withId(R.id.deck_name)).check(matches(withText(deckName))));
        onView(withId(R.id.deck_name)).perform(replaceText(newDeckName), closeSoftKeyboard());
        pressBack();
        waitView(() -> onView(withText(newDeckName)).check(matches(hasSibling(withText("0")))));
        deleteDeck(newDeckName);
    }
}
