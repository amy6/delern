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

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

public final class WaitView {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaitView.class);
    private static final long TIMEOUT = 5000;
    private static final long POLL_INTERVAL = 100;

    private static void sleep(long millis) {
        onView(isRoot()).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "sleep for " + millis + "ms";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        });
    }

    /**
     * Like onView, but waits for a view matcher to trigger.
     *
     * @param viewMatcher matcher for onView()
     * @return see onView()
     */
    public static ViewInteraction waitView(final Matcher<View> viewMatcher) {
        long startTime = System.currentTimeMillis();
        ViewInteraction vi = onView(viewMatcher);
        while (true) {
            try {
                LOGGER.info("Attempting to locate View {}", viewMatcher);
                return vi.check((targetView, missingViewException) -> {
                    if (targetView == null) {
                        throw missingViewException;
                    }
                });
            } catch (NoMatchingViewException e) {
                if (startTime + TIMEOUT >= System.currentTimeMillis()) {
                    sleep(POLL_INTERVAL);
                } else {
                    LOGGER.error("Giving up locating View {}", viewMatcher);
                    throw e;
                }
            }
        }
    }

    public static <T extends Activity> void bringToFront(final ActivityTestRule<T> rule) {
        final T runningActivity = rule.getActivity();
        if (runningActivity == null) {
            rule.launchActivity(null);
        } else {
            final Intent intent = new Intent(runningActivity, runningActivity.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            runningActivity.startActivity(intent);
        }
    }
}
