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

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.test.DeckPostfix;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.dasfoo.delern.test.FirebaseSignInRule;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.BasicOperations.createCard;
import static org.dasfoo.delern.test.BasicOperations.createDeck;
import static org.dasfoo.delern.test.BasicOperations.deleteDeck;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Test Navigation Drawer
 */
public class SharingTest {

    private String mAliceEmail;
    private String mDeckName;

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

    @Before
    public void getUsersEmails() {
        //waitView(() -> onView(withId(R.id.sign_in_button)).check(matches(isDisplayed())));
        // Get Alice email
        mAliceEmail = mSignInRule.signIn("alice");
        signOut();
        // Sign In as Bob to share deck.
        mSignInRule.signIn("bob");
    }

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
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        // Create Deck
        mDeckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        createDeck(mDeckName);
        String front1 = "front1";
        String back1 = "back1";
        String front2 = "front2";
        String back2 = "back2";
        createCard(front1, back1, /* reversed= */false);
        createCard(front2, back2, /* reversed= */false);
        pressBack();
        // Choose "Can edit" spinner
        Context context = mActivityRule.getActivity().getApplicationContext();
        String sharingAccessEdit = context.getResources()
                .getString(R.string.can_edit_text);
        shareDeck(mAliceEmail, "alice", sharingAccessEdit);
        signOut();
        // Alice sign in to check shared deck
        mSignInRule.signIn("alice");
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        waitView(() -> onView(withText(mDeckName)).check(matches(hasSibling(withText("2")))));
        // Check that sharing is disabled
        checkDisabledSharing();
        // Check that cards available for learning
        learnSharedCards(front1, front2);
        checkPermissionsInLearningWithWriteAccess(front2, back2);
        pressBack();
        // Check that list of cards is displayed and "add button" shows user message
        // that operation is enabled.
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        // Test adding cards
        waitView(() -> onView(withText(R.string.edit_cards_deck_menu)).perform(click()));
        waitView(() -> onView(withId(R.id.f_add_card_button)).perform(click()));
        waitView(() -> onView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()))
                .perform(closeSoftKeyboard()));
        // Out from  Add Activity.
        pressBack();
        //  Check that deleting and updating card is enabled. Open preview of card.
        waitView(() -> onView(allOf(withText(front1), hasSibling(withText(back1))))
                .perform(click()));
        // Edit is enabled
        waitView(() -> onView(withId(R.id.edit_card_button)).check(matches(isDisplayed()))
                .perform(click()));
        waitView(() -> onView(withId(R.id.front_side_text)).check(matches(withText(front1))));
        onView(withId(R.id.back_side_text)).check(matches(withText(back1)))
                .perform(closeSoftKeyboard());
        // Out from Edit Activity
        pressBack();
        // Deleting is is enabled
        onView(withId(R.id.delete_card_menu)).perform(click());
        onView(withText(R.string.delete)).check(matches(isDisplayed()));
        onView(withText(R.string.cancel)).perform(click());
        pressBack();
        pressBack();

        signOut();
        // Bob sign in
        mSignInRule.signIn("bob");
        deleteDeck(mDeckName);
    }

    @Test
    public void shareDeckWithReadAccess() {
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        // Create Deck
        mDeckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        createDeck(mDeckName);
        String front1 = "front1";
        String back1 = "back1";
        createCard(front1, back1, /* reversed= */false);
        String front2 = "front2";
        String back2 = "back2";
        createCard(front2, back2, /* reversed= */ false);
        pressBack();
        // Share deck.
        Context context = mActivityRule.getActivity().getApplicationContext();
        String sharingAccessView = context.getResources()
                .getString(R.string.can_view_text);
        shareDeck(mAliceEmail, "alice", sharingAccessView);
        signOut();
        // Alice sign in to check shared deck
        mSignInRule.signIn("alice");
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        // Check that shared deck exists
        waitView(() -> onView(withText(mDeckName)).check(matches(hasSibling(withText("2")))));
        // Check that sharing is disabled
        checkDisabledSharing();
        // Check that cards available for learning
        learnSharedCards(front1, front2);
        checkPermissionsInLearningWithReadAccess();
        pressBack();
        // Check that list of cards is displayed and "add button" shows user message
        // that operation is disabled.
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        waitView(() -> onView(withText(R.string.edit_cards_deck_menu)).perform(click()));
        waitView(() -> onView(withId(R.id.f_add_card_button)).perform(click()));
        waitView(() -> onView(withText(R.string.add_cards_with_read_access_user_warning))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed())));
        // Check that deleting and updating card is disabled
        onView(allOf(withText(front1), hasSibling(withText(back1)))).perform(click());
        // Edit is disabled
        waitView(() -> onView(withId(R.id.edit_card_button)).check(matches(isDisplayed()))
                .perform(click()));
        waitView(() -> onView(withText(R.string.edit_cards_with_read_access_user_warning))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed())));
        // Deleting is disabled is disabled
        onView(withId(R.id.delete_card_menu)).perform(click());
        waitView(() -> onView(withText(R.string.delete_cards_with_read_access_user_warning))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed())));
        pressBack();
        pressBack();
        signOut();
        // Bob sign in
        mSignInRule.signIn("bob");
        deleteDeck(mDeckName);
    }

    /**
     * Performs sharing testing. After sharing it returns to parent activity.
     *
     * @param userEmail     email of the user with who to share a deck.
     * @param userName      name of the user
     * @param sharingAccess access that should be given to the user
     */
    private void shareDeck(final String userEmail, final String userName,
                           final String sharingAccess) {
        // Open ShareActivity
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        waitView(() -> onView(withText(R.string.deck_share_menu)).perform(click()));
        waitView(() -> onView(withId(R.id.person_data)).check(matches(isDisplayed())));
        // Type email address
        onView(withId(R.id.person_data)).perform(typeText(userEmail), closeSoftKeyboard());
        // Choose permission in spinner
        // Spinner doesn't always open.
        onView(withId(R.id.sharing_permissions_spinner)).perform(click());
        onData(CoreMatchers.allOf(is(instanceOf(String.class)), is(sharingAccess)))
                .perform(click());
        // TODO(ksheremet): Check that right permission was chosen.
        /*onView(allOf(withId(R.id.sharing_permissions_spinner), hasSibling(withText(aliceEmail))))
                .check(matches(withDrawable(R.drawable.ic_create_black_24dp)));*/
        // Share deck
        onView(withId(R.id.share_deck_menu)).perform(click());
        // Name of the user with who was shared a deck appears with delay. It can be flaky test.
        waitView(() -> onView(withText(userName)).check(matches(isDisplayed())));
        // TODO(ksheremet): Check that user got right access.
        /*onView(allOf(withText("alice"), hasSibling(withDrawable(R.drawable.ic_create_black_24dp))))
                .check(matches(isDisplayed()));*/
        pressBack();
    }

    /**
     * Checks that leaning is available for shared cards. After completion stays in
     * learning activity.
     *
     * @param front1 front side of the first card.
     * @param front2 front side of the second card.
     */
    private void learnSharedCards(final String front1, final String front2) {
        // Start Learning Activity
        waitView(() -> onView(allOf(withText(mDeckName), hasSibling(withText("2"))))
                .perform(click()));
        // Check the first card
        waitView(() -> onView(withId(R.id.textFrontCardView)).check(matches(withText(front1))));
        // Turn card
        onView(withId(R.id.turn_card_button)).perform(click());
        onView(withId(R.id.to_know_button)).perform(click());
        // Check the second card
        waitView(() -> onView(withId(R.id.textFrontCardView)).check(matches(withText(front2))));
    }

    /**
     * Checks permissions in learning that available with view access.
     * Edit and delete card must be disabled.
     * It performs in Learning Activity.
     */
    private void checkPermissionsInLearningWithReadAccess() {
        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // Check that deletion of card is disabled
        onView(withText(R.string.delete)).perform(click());
        waitView(() -> onView(withText(R.string.delete_cards_with_read_access_user_warning))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed())));
        // Check that editing of card is disabled
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.edit)).perform(click());
        waitView(() -> onView(withText(R.string.edit_cards_with_read_access_user_warning))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed())));
    }

    /**
     * Checks permissions in learning that available with write access.
     * Edit and delete of card are enabled.
     * It performs in Learning Activity.
     */
    private static void checkPermissionsInLearningWithWriteAccess(final String front,
                                                                  final String back) {
        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // Check that deletion of card is disabled
        onView(withText(R.string.delete)).perform(click());
        onView(withText(R.string.delete)).check(matches(isDisplayed()));
        onView(withText(R.string.cancel)).perform(click());
        // Check that editing of card is disabled
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.edit)).perform(click());
        waitView(() -> onView(withId(R.id.front_side_text)).check(matches(withText(front))));
        onView(withId(R.id.back_side_text)).check(matches(withText(back)))
                .perform(closeSoftKeyboard());
        // Out from Edit Activity
        pressBack();
    }

    private void checkDisabledSharing() {
        // Check that sharing is disabled
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        // not(isEnabled) doesn't work with Popup menu. To check that menu "Share" is disabled,
        // click on it and check that nothing happened.
        waitView(() -> onView(withText(R.string.deck_share_menu)).perform(click()));
        waitView(() -> onView(withText(R.string.share_cards_with_no_access_user_warning))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed())));
    }
}
