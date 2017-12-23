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

package org.dasfoo.delern.test;

import android.text.InputType;

import org.dasfoo.delern.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Handle basic operations such as create deck, delete deck, create card and etc.
 */
public final class BasicOperations {

    /**
     * Adds card to DB, check that fields are getting empty after adding.
     *
     * @param frontSide front side of card.
     * @param backSide  back side of card.
     * @param reversed  true to create reversed card as well, otherwise - false.
     */
    public static void createCard(final String frontSide, final String backSide,
                                  final boolean reversed) {
        waitView(() -> onView(withId(R.id.add_card_to_db)).check(matches(isDisplayed())));
        onView(withId(R.id.front_side_text)).perform(typeText(frontSide), closeSoftKeyboard());
        onView(withId(R.id.back_side_text)).perform(typeText(backSide), closeSoftKeyboard());
        if (reversed) {
            onView(withId(R.id.add_reversed_card_checkbox)).perform(click());
        }
        onView(withId(R.id.add_card_to_db)).perform(click());
        // Check that fields are empty after adding card
        waitView(() -> onView(withId(R.id.front_side_text)).check(matches(withText(""))));
        onView(withId(R.id.back_side_text)).check(matches(withText("")));
    }

    /**
     * Deletes deck by name.
     *
     * @param deckName name of deck.
     */
    public static void deleteDeck(final String deckName) {
        waitView(() -> onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click()));
        deleteSelectedDeck();
    }

    /**
     * Deletes current deck from settings activity.
     */
    public static void deleteSelectedDeck() {
        onView(withText(R.string.deck_settings_menu)).perform(click());
        waitView(() -> onView(withId(R.id.delete_deck_menu)).perform(click()));
        onView(withText(R.string.delete)).perform(click());
    }

    /**
     * Creates deck in DelernMainActivity. After creation it lands in AddEditCardActivity.
     *
     * @param deckName name of deck.
     */
    public static void createDeck(String deckName) {
        waitView(() -> onView(withId(R.id.fab)).perform(click()));
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(deckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        waitView(() -> onView(withId(R.id.add_card_to_db))
                .check(matches(isDisplayed()))
                .perform(closeSoftKeyboard()));
    }
}
