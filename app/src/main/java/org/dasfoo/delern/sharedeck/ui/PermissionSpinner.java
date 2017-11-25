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

package org.dasfoo.delern.sharedeck.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.util.Consumer;

/**
 * Spinner (aka dropdown) for Deck Access modes.
 */
public class PermissionSpinner extends AppCompatSpinner {
    /**
     * Create a new instance of the spinner.
     *
     * @param context {@link android.widget.Spinner}
     * @param attrs   {@link android.widget.Spinner}
     */
    public PermissionSpinner(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Define set of available values for the spinner.
     *
     * @param textSpinner text values, defines items in the spinner.
     * @param imgSpinner  image values, may be shorter than text - will be replaced by text.
     */
    public void setType(final int textSpinner, final int imgSpinner) {
        setAdapter(new PermissionSpinnerAdapter(getContext(), textSpinner, imgSpinner));
    }

    /**
     * Set a listener for when permission is selected.
     *
     * @param callback called whenever an item is selected.
     */
    public void setOnItemSelectedListener(final Consumer<String> callback) {
        setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view,
                                       final int position, final long l) {
                Context context = view.getContext();
                String[] sharingArrayOption = context.getResources()
                        .getStringArray(R.array.user_permissions_spinner_text);
                if (sharingArrayOption[position].equals(context
                        .getResources()
                        .getString(R.string.can_edit_text))) {
                    callback.accept("write");
                }
                if (sharingArrayOption[position].equals(context.getResources()
                        .getString(R.string.can_view_text))) {
                    callback.accept("read");
                }
                if (sharingArrayOption[position].equals(context
                        .getResources().getString(R.string.no_access_text))) {
                    callback.accept("");
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                // No need for implementation
            }
        });
    }
}
