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
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.text.InputType;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.test.DeckPostfix;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.dasfoo.delern.test.FirebaseSignInRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.AllOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
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
import static org.dasfoo.delern.test.BasicOperations.createCard;
import static org.dasfoo.delern.test.BasicOperations.deleteDeck;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;


/**
 * Test Navigation Drawer
 */
public class SharingTest {

    @Rule
    public IntentsTestRule<DelernMainActivity> mActivityRule = new IntentsTestRule<>(
            DelernMainActivity.class);

    @Rule
    public FirebaseOperationInProgressRule mFirebaseRule =
            new FirebaseOperationInProgressRule(true);

    @Rule
    public FirebaseSignInRule mSignInRule = new FirebaseSignInRule(false);

    @Rule
    public TestName mName = new TestName();

    private static void signOut() {
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        waitView(() -> onView(withId(R.id.nav_view)).check(matches(isDisplayed())));
        waitView(() -> onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_sign_out)));
        onView(withText(R.string.sign_out)).check(matches(isDisplayed()));
        onView(withText(R.string.sign_out)).perform(click());
        waitView(() -> onView(withId(R.id.sign_in_button)).check(matches(isDisplayed())));
    }

    @Test
    public void shareDeckWithEditAccess() {
        waitView(() -> onView(withId(R.id.sign_in_button)).check(matches(isDisplayed())));

        String aliceEmail = mSignInRule.signIn("alice");
        signOut();

        String bobEmail = mSignInRule.signIn("bob");
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        // Create Deck
        String deckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(() -> onView(withId(R.id.fab)).perform(click()));
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView(deckName), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        waitView(() -> onView(withId(R.id.add_card_to_db))
                .check(matches(isDisplayed()))
                .perform(closeSoftKeyboard()));
        String front1 = "front1";
        String back1 = "back1";
        createCard(front1, back1, /* reversed= */true);
        pressBack();
        // Open ShareActivity
        onView(AllOf.allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click());
        waitView(() -> onView(withText(R.string.deck_share_menu)).perform(click()));
        waitView(() -> onView(withId(R.id.person_data)).check(matches(isDisplayed())));
        // Type email address
        onView(withId(R.id.person_data)).perform(typeText(aliceEmail), closeSoftKeyboard());
        // Choose "Can edit" spinner
        Context context = mActivityRule.getActivity().getApplicationContext();
        String sharingAccessEdit = context.getResources()
                .getString(R.string.can_edit_text);
        // Spinner doesn't always open.
        onView(withId(R.id.sharing_permissions_spinner)).perform(click());
        onData(CoreMatchers.allOf(is(instanceOf(String.class)), is(sharingAccessEdit)))
                .perform(click());
        // TODO(ksheremet): Check that right permission was chosen.
        /*onView(allOf(withId(R.id.sharing_permissions_spinner), hasSibling(withText(aliceEmail))))
                .check(matches(withDrawable(R.drawable.ic_create_black_24dp)));*/
        // Share deck
        onView(withId(R.id.share_deck_menu)).perform(click());
        waitView(() -> onView(withText("alice")).check(matches(isDisplayed())));
        // TODO(ksheremet): Check that alice got edit access.
        /*onView(allOf(withText("alice"), hasSibling(withDrawable(R.drawable.ic_create_black_24dp))))
                .check(matches(isDisplayed()));*/
        pressBack();
        signOut();
        // Alice sign in to check shared deck
        mSignInRule.signIn("alice");
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        waitView(() -> onView(withText(deckName)).check(matches(hasSibling(withText("2")))));
        // Check that sharing is disabled
        onView(AllOf.allOf(withId(R.id.deck_popup_menu), hasSibling(withText(deckName))))
                .perform(click());
        // not(isEnabled) doesn't work with Popup menu. To check that menu "Share" is disabled,
        // click on it and check that nothing happened.
        waitView(() -> onView(withText(R.string.deck_share_menu)).perform(click()));
        waitView(() -> onView(withText(R.string.deck_share_menu)).check(matches(isDisplayed())));
        pressBack();
        // Check that cards available for learning
        // Start Learning Activity
        waitView(() -> onView(AllOf.allOf(withText(deckName), hasSibling(withText("2"))))
                .perform(click()));
        // Check the first card
        waitView(() -> onView(withId(R.id.textFrontCardView)).check(matches(withText(front1))));
        pressBack();
        signOut();
        // Bob sign in
        mSignInRule.signIn("bob");
        deleteDeck(deckName);
    }
}
