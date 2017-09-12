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

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.text.InputType;
import android.widget.EditText;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.dasfoo.delern.util.DeckPostfix;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.core.AllOf.allOf;


/**
 * Tests cards list.
 */
public class CardListTest {
    @Rule
    public ActivityTestRule<DelernMainActivity> mActivityRule = new ActivityTestRule<>(
            DelernMainActivity.class);

    @Rule
    public FirebaseOperationInProgressRule mFirebaseRule = new FirebaseOperationInProgressRule();

    @Rule
    public TestName mName = new TestName();

    private String mDeckName;

    @Before
    public void createDeck() {
        mDeckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(withId(R.id.fab)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(mDeckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
    }

    private static void createCard(final String frontSide, final String backSide) {
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        onView(withId(R.id.front_side_text)).perform(typeText(frontSide));
        onView(withId(R.id.back_side_text)).perform(typeText(backSide), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        // Check that fields are empty after adding card
        waitView(withId(R.id.front_side_text)).check(matches(withText("")));
        onView(withId(R.id.back_side_text)).check(matches(withText("")));
    }

    @Test
    public void searchCard() {
        String front1 = "die Tochter";
        String back1 = "die Tochter2";
        String front2 = "der Hund";
        String back2 = "der Hund2";
        createCard(front1, back1);
        createCard(front2, back2);
        pressBack();
        // Change deckType
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.edit)).perform(click());
        Context context = mActivityRule.getActivity().getApplicationContext();
        onView(withId(R.id.number_of_cards))
                .check(matches(withText(String.format(context.getString(R.string.number_of_cards),
                        2))));
        waitView(withId(R.id.search_action)).perform(click());
        onView(isAssignableFrom(EditText.class)).perform(typeText("die"),
                closeSoftKeyboard());
        waitView(withId(R.id.number_of_cards))
                .check(matches(withText(String.format(context.getString(R.string.number_of_cards),
                        1))));
        onView(withText(front1)).check(matches(hasSibling(withText(back1))));
        pressBack();
        pressBack();
    }

    @After
    public void deleteDeck() {
        waitView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.delete)).perform(click());
        onView(withText(R.string.delete)).perform(click());
    }
}
