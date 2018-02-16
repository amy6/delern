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

package org.dasfoo.delern.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.dasfoo.delern.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;


/**
 * Shows OnBoarding when a user opens the app first time.
 */
public class OnBoardingActivity extends MaterialIntroActivity {

    /**
     * Starts OnBoardingActivity.
     *
     * @param context context to start Activity.
     */
    public static void startActivity(final Context context) {
        Intent intent = new Intent(context, OnBoardingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.cards)
                .title(getString(R.string.create_deck_onboarding_activity_title))
                .description(getString(R.string.create_deck_onboarding_activity_description))
                .build());
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.learning_onboarding)
                .title(getString(R.string.learn_cards_onboarding_activity_title))
                .description(getString(R.string.learn_cards_onboarding_activity_description))
                .build());
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.friends)
                .title(getString(R.string.share_with_friends_onboarding_activity))
                .description(getString(R.string.share_with_friends_onboarding_description))
                .build());
    }
}
