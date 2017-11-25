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


import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.test.FirebaseOperationInProgressRule;
import org.dasfoo.delern.test.FirebaseSignInRule;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.dasfoo.delern.test.WaitView.waitView;


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

    @Test
    public void signInAliceAndBob() {
        waitView(() -> onView(withId(R.id.sign_in_button)).check(matches(isDisplayed())));

        String aliceEmail = mSignInRule.signIn("alice");
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        waitView(() -> onView(withId(R.id.nav_view)).check(matches(isDisplayed())));
        waitView(() -> onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_sign_out)));
        onView(withText(R.string.sign_out)).check(matches(isDisplayed()));
        onView(withText(R.string.sign_out)).perform(click());

        waitView(() -> onView(withId(R.id.sign_in_button)).check(matches(isDisplayed())));

        String bobEmail = mSignInRule.signIn("bob");
        waitView(() -> onView(withId(R.id.fab)).check(matches(isDisplayed())));
    }
}
