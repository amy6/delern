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

package org.dasfoo.delern.util;

import android.content.Context;
import android.graphics.Typeface;

import com.getkeepsafe.taptargetview.TapTarget;

import org.dasfoo.delern.R;

/**
 * Specifies standard style for onBoarding.
 */
public final class OnBoardingStyle {

    private OnBoardingStyle() {
        // a private constructor to prevent instantiation
    }

    /**
     * Specifies default styles for onBoarding.
     *
     * @param tapTarget variable for onBoarding.
     * @param context   context for getting parameters.
     * @return ViewTapTarget with applied styles.
     */
    public static TapTarget setDefStyle(final TapTarget tapTarget, final Context context) {
        return tapTarget
                .outerCircleColor(R.color.colorPrimaryDark /* Color for the outer circle */)
                .targetCircleColor(android.R.color.white /* Color for the target circle */)
                .titleTextSize(context.getResources()
                        .getInteger(R.integer.title_text_size_onboarding) /* Size (in sp) */)
                .titleTextColor(android.R.color.white /* Color of the title text */)
                .descriptionTextSize(context.getResources()
                        .getInteger(R.integer.description_text_size_onboarding))
                .textColor(android.R.color.white
                        /* Color for both the title and description text */)
                .textTypeface(Typeface.SANS_SERIF /* Typeface for the text */)
                .dimColor(android.R.color.black /* it will dim behind the view with 30% opacity */)
                .drawShadow(true /* Whether to draw a drop shadow or not */)
                .cancelable(true /* Whether tapping outside the outer circle dismisses the view */)
                .tintTarget(true /* Whether to tint the target view's color */)
                .transparentTarget(true
                        /* if target is transparent displays the content underneath*/);
    }
}
