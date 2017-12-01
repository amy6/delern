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
import android.support.test.runner.AndroidJUnit4;
import android.text.InputType;

import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.test.DeckPostfix;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.dasfoo.delern.test.FirebaseSignInRule;
import org.dasfoo.delern.test.ViewMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.BasicOperations.createCard;
import static org.dasfoo.delern.test.BasicOperations.deleteDeck;
import static org.dasfoo.delern.test.WaitView.bringToFront;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Test for Firebase Offline sync.
 */
@RunWith(AndroidJUnit4.class)
public class OfflineTest {

    // TODO(dotdoom): change this from being a copy of LearningTest to a jUnit @Rule or a test
    //                runner, which will retry test cases in offline to see whether they pass.

    @Rule
    public ActivityTestRule<DelernMainActivity> mActivityRule = new ActivityTestRule<>(
            DelernMainActivity.class);

    @Rule
    public TestName mName = new TestName();

    @Rule
    public FirebaseOperationInProgressRule mFirebaseRule =
            new FirebaseOperationInProgressRule(false);

    @Rule
    public FirebaseSignInRule mSignInRule = new FirebaseSignInRule(true);

    private String mDeckName;

    private void changeDeckType(final DeckType dType) {
        Context context = mActivityRule.getActivity().getApplicationContext();
        String deckType = context.getResources()
                .getStringArray(R.array.deck_type_spinner)[dType.ordinal()];
        onView(CoreMatchers.allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.deck_settings_menu)).perform(click());
        // Spinner doesn't always open.
        onView(withId(R.id.deck_type_spinner)).perform(click());
        onData(CoreMatchers.allOf(is(instanceOf(String.class)), is(deckType))).perform(click());
        onView(withId(R.id.deck_type_spinner))
                .check(matches(withSpinnerText(is(deckType))));
        pressBack();
    }

    @Before
    public void createDeck() {
        FirebaseDatabase.getInstance().goOffline();

        mDeckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(() -> onView(withId(R.id.fab)).perform(click()));
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(mDeckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
    }

    @Test
    public void learnGermanCards() {
        String front1 = "mother";
        String back1 = "die Mutter";
        String front2 = "father";
        String back2 = "der Vater";
        String front3 = "kid";
        String back3 = "das Kind";
        createCard(front1, back1, /* reversed= */false);
        createCard(front2, back2, /* reversed= */false);
        createCard(front3, back3, /* reversed= */false);
        pressBack();

        // Change deckType
        changeDeckType(DeckType.GERMAN);

        // Start Learning Activity
        waitView(() -> onView(allOf(withText(mDeckName), hasSibling(withText("3"))))
                .perform(click()));
        // Check the first card
        waitView(() -> onView(withId(R.id.textFrontCardView)).check(matches(withText(front1))));
        onView(withId(R.id.card_view)).check(matches(new ViewMatchers.ColorMatcher(R.color.feminine)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        onView(withId(R.id.textBackCardView)).check(matches(withText(back1)));
        onView(withId(R.id.to_know_button)).perform(click());

        FirebaseDatabase.getInstance().goOnline();
        mFirebaseRule.enableForCurrentTestCase();

        // Check the second card
        waitView(() -> onView(withId(R.id.textFrontCardView)).check(matches(withText(front2))));
        onView(withId(R.id.card_view)).check(matches(new ViewMatchers.ColorMatcher(R.color.masculine)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(back2)));
        onView(withId(R.id.to_repeat_button)).perform(click());
        // Check the third card
        waitView(() -> onView(withId(R.id.textFrontCardView)).check(matches(withText(front3))));
        onView(withId(R.id.card_view)).check(matches(new ViewMatchers.ColorMatcher(R.color.neuter)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(back3)));
        onView(withId(R.id.to_repeat_button)).perform(click());
    }

    @After
    public void delete() {
        bringToFront(mActivityRule);
        deleteDeck(mDeckName);
    }
}
