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

package org.dasfoo.delern.listeners;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Listen data changes in Firebase node.
 */
public abstract class AbstractOnDataChangeListener implements ValueEventListener {
    private final String mTag;
    private final Context mContext;

    /**
     * Constructor for listener on data change.
     * If changes were cancelled, writes message to user
     *
     * @param tag for logging.
     * @param context for writing user messages
     */
    public AbstractOnDataChangeListener(final String tag, final Context context) {
        this.mTag = tag;
        this.mContext = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void onDataChange(DataSnapshot dataSnapshot);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCancelled(final DatabaseError databaseError) {
        Log.e(mTag, "Firebase operation is cancelled:", databaseError.toException());
        Crashlytics.logException(databaseError.toException());
        Toast.makeText(mContext, databaseError.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
