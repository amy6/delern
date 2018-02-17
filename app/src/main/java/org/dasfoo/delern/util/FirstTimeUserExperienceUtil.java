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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.Nullable;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.dasfoo.delern.R;

/**
 * Specifies standard style for onBoarding.
 */
public final class FirstTimeUserExperienceUtil {

    private final Activity mActivity;
    private final SharedPreferences mSharedPreferences;
    private final int mPreferenceKey;

    /**
     * Constructor.Performs initialization of parameters.
     *
     * @param prefKey  preference key to check OnBoarding for the Activity.
     * @param activity activity for showing onBoarding.
     */
    public FirstTimeUserExperienceUtil(final Activity activity, final int prefKey) {
        this.mActivity = activity;
        this.mPreferenceKey = prefKey;
        mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    /**
     * Specifies default styles for onBoarding.
     *
     * @param tapTarget variable for onBoarding.
     * @return ViewTapTarget with applied styles.
     */
    private TapTarget setDefStyle(final TapTarget tapTarget) {
        return tapTarget
                .outerCircleColor(R.color.colorPrimaryDark /* Color for the outer circle */)
                .targetCircleColor(android.R.color.white /* Color for the target circle */)
                .titleTextSize(mActivity.getResources()
                        .getInteger(R.integer.title_text_size_onboarding) /* Size (in sp) */)
                .titleTextColor(android.R.color.white /* Color of the title text */)
                .descriptionTextSize(mActivity.getResources()
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

    /**
     * Checks whether activity is opened first time. If yes, it shows onBoarding.
     *
     * @return true if OnBoarding was already shown, otherwise false.
     */
    public boolean isOnBoardingShown() {
        boolean defValue = mActivity.getResources()
                .getBoolean(R.bool.is_onboarding_showed_default);
        return mSharedPreferences.getBoolean(mActivity.getString(mPreferenceKey), defValue);
    }

    /**
     * Shows OnBoarding for a target view.
     *
     * @param tapTarget an information about view that needs OnBoarding.
     * @param listener  listener to handle onClick.
     */
    public void showOnBoarding(final TapTarget tapTarget,
                               @Nullable final TapTargetView.Listener listener) {
        if (listener == null) {
            TapTargetView.showFor(/* Activity */mActivity, setDefStyle(tapTarget));
        } else {
            TapTargetView.showFor(/* Activity */mActivity, setDefStyle(tapTarget), listener);
        }
        onBoardingShown();
    }

    /**
     * Sets that onBoarding was shown in Preferences.
     */
    public void onBoardingShown() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mActivity.getString(mPreferenceKey), true);
        editor.apply();
    }
}
