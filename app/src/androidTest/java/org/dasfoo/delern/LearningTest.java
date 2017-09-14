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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.View;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.dasfoo.delern.util.DeckPostfix;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.WaitView.waitView;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Test learning cards functions.
 */
@RunWith(AndroidJUnit4.class)
public class LearningTest {

    @Rule
    public ActivityTestRule<DelernMainActivity> mActivityRule = new ActivityTestRule<>(
            DelernMainActivity.class);

    @Rule
    public TestName mName = new TestName();

    @Rule
    public FirebaseOperationInProgressRule mFirebaseRule = new FirebaseOperationInProgressRule();

    private String mDeckName;

    private static void createCard(final String frontSide, final String backSide) {
        waitView(withId(R.id.add_card_to_db)).check(matches(isDisplayed()));
        onView(withId(R.id.front_side_text)).perform(typeText(frontSide));
        onView(withId(R.id.back_side_text)).perform(typeText(backSide), closeSoftKeyboard());
        onView(withId(R.id.add_card_to_db)).perform(click());
        // Check that fields are empty after adding card
        waitView(withId(R.id.front_side_text)).check(matches(withText("")));
        onView(withId(R.id.back_side_text)).check(matches(withText("")));
    }

    @Before
    public void createDeck() {
        mDeckName = mName.getMethodName() + DeckPostfix.getRandomNumber();
        waitView(withId(R.id.fab)).perform(click());
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
        createCard(front1, back1);
        createCard(front2, back2);
        createCard(front3, back3);
        pressBack();
        // Change deckType
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.cards_type)).perform(click());
        onView(withText(R.string.basic_cards_type)).check(matches(not(isClickable())));
        onView(withText(R.string.german_cards_type)).perform(click());
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("3"))))
                .perform(click());
        // Check the first card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front1)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.feminine)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        onView(withId(R.id.textBackCardView)).check(matches(withText(back1)));
        onView(withId(R.id.to_know_button)).perform(click());
        // Check the second card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front2)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.masculine)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(back2)));
        onView(withId(R.id.to_repeat_button)).perform(click());
        // Check the third card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front3)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.neuter)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(back3)));
        onView(withId(R.id.to_repeat_button)).perform(click());
    }

    @Test
    public void learnSwissCards() {
        String front1 = "mother";
        String back1 = "d Muetter";
        String front2 = "father";
        String back2 = "de Vater";
        String front3 = "kid";
        String back3 = "s Kind";
        createCard(front1, back1);
        createCard(front2, back2);
        createCard(front3, back3);
        pressBack();
        // Change deckType
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.cards_type)).perform(click());
        onView(withText(R.string.basic_cards_type)).check(matches(not(isClickable())));
        onView(withText(R.string.swissgerman_cards_type)).perform(click());
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("3"))))
                .perform(click());
        // Check the first card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front1)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.feminine)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        onView(withId(R.id.textBackCardView)).check(matches(withText(back1)));
        onView(withId(R.id.to_know_button)).perform(click());
        // Check the second card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front2)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.masculine)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(back2)));
        onView(withId(R.id.to_repeat_button)).perform(click());
        // Check the third card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front3)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.neuter)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(back3)));
        onView(withId(R.id.to_repeat_button)).perform(click());
    }

    @Test
    public void learnBasicCard() {
        String front = "mother";
        String back = "die Mutter";
        createCard(front, back);

        pressBack();
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("1"))))
                .perform(click());
        // Check the card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.noGender)));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        onView(withId(R.id.textBackCardView)).check(matches(withText(back)));
        onView(withId(R.id.to_know_button)).perform(click());
    }

    @Test
    public void learnBasicCardsCheckingWatchedProgress() {
        Context context = mActivityRule.getActivity().getApplicationContext();
        String front1 = "card1";
        String back1 = "backCard1";
        String front2 = "card2";
        String back2 = "backCard2";
        String front3 = "card3";
        String back3 = "backCard3";
        createCard(front1, back1);
        createCard(front2, back2);
        createCard(front3, back3);
        pressBack();
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("3"))))
                .perform(click());
        // Check the first card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front1)));
        onView(withId(R.id.learned_in_session))
                .check(matches(withText(String.format(context.getString(R.string.card_watched_text),
                        0))));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        onView(withId(R.id.textBackCardView)).check(matches(withText(back1)));
        onView(withId(R.id.to_know_button)).perform(click());
        // Check the second card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front2)));
        onView(withId(R.id.learned_in_session))
                .check(matches(withText(String.format(context.getString(R.string.card_watched_text),
                        1))));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(back2)));
        onView(withId(R.id.to_repeat_button)).perform(click());
        // Check the third card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front3)));
        onView(withId(R.id.learned_in_session))
                .check(matches(withText(String.format(context.getString(R.string.card_watched_text),
                        2))));
        // Flip card
        onView(withId(R.id.turn_card_button)).perform(click());
        // Check back side of card
        onView(withId(R.id.textBackCardView)).check(matches(withText(back3)));
        onView(withId(R.id.to_repeat_button)).perform(click());
    }

    @Test
    public void deleteCardMenuOption() {
        String front = "mother";
        String back = "die Mutter";
        createCard(front, back);
        pressBack();
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("1"))))
                .perform(click());
        // Check the card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front)));
        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.delete)).perform(click());
        onView(withText(R.string.delete)).perform(click());
    }

    @Test
    public void basicDeckType() {
        String front1 = "mother";
        String back1 = "d Muetter";
        createCard(front1, back1);
        pressBack();
        // Change deckType
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.cards_type)).perform(click());
        onView(withText(R.string.basic_cards_type)).check(matches(not(isClickable())));
        onView(withText(R.string.swissgerman_cards_type)).perform(click());
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("1"))))
                .perform(click());
        // Check the first card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front1)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.feminine)));
        pressBack();
        // Change deckType
        onView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.cards_type)).perform(click());
        onView(withText(R.string.swissgerman_cards_type)).check(matches(not(isClickable())));
        onView(withText(R.string.basic_cards_type)).perform(click());
        // Start Learning Activity
        waitView(allOf(withText(mDeckName), hasSibling(withText("1"))))
                .perform(click());
        // Check the first card
        waitView(withId(R.id.textFrontCardView)).check(matches(withText(front1)));
        onView(withId(R.id.card_view)).check(matches(new ColorMatcher(R.color.noGender)));
        pressBack();
    }

    @After
    public void deleteDeck() {
        waitView(allOf(withId(R.id.deck_popup_menu), hasSibling(withText(mDeckName))))
                .perform(click());
        onView(withText(R.string.delete)).perform(click());
        onView(withText(R.string.delete)).perform(click());
    }

    public static class ColorMatcher extends BaseMatcher<View> {

        private final int matchColor;

        /*default*/ ColorMatcher(int matchColor) {
            this.matchColor = matchColor;
        }

        @Override
        public boolean matches(Object item) {
            Context context = ((View) item).getContext();
            int settingsColor = ContextCompat.getColor(context, this.matchColor);
            return settingsColor == ((CardView) item).getCardBackgroundColor().getDefaultColor();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("with background color: ");
        }
    }
}
