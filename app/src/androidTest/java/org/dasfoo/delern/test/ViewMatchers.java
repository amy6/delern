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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Custom ViewMatchers for Espresso, to be chained into the matching sequence e.g.:
 * onView(ViewMatchers.first(withId(R.id.fab)))...
 */
public final class ViewMatchers {
    /**
     * Returns a custom matcher that retrieves the first matching item only.
     *
     * @param matcher matcher that may match more than one item.
     * @param <T>     matcher type.
     * @return custom matcher which will only trigger for the first item.
     */
    public static <T> Matcher<T> first(final Matcher<T> matcher) {
        return new BaseMatcher<T>() {
            private boolean isFirst = true;

            @Override
            public boolean matches(Object item) {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false;
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                matcher.describeTo(description);
                description.appendText("first matching item only");
            }
        };
    }

    /**
     * Check background colors of CardView.
     */
    public static final class ColorMatcher extends BaseMatcher<View> {
        private final int matchColor;

        public ColorMatcher(int matchColor) {
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
