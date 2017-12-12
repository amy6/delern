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

package org.dasfoo.delern.aboutapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import org.dasfoo.delern.R;


/**
 * Initializes Shared Preferences.
 */
public class AboutAppFragment extends PreferenceFragmentCompat {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.pref_about_app);
        findPreference(getString(R.string.pref_key_license))
                .setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(getContext(), OssLicensesMenuActivity.class);
                    intent.putExtra("title", getString(R.string.pref_summary_licenses));
                    startActivity(intent);
                    return true;
                });
    }
}
