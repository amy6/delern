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

package org.dasfoo.delern.models.listeners;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Listen data changes in Firebase node.
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractOnFBDataChangeListener implements ValueEventListener {
    private final AbstractDataAvailableListener mOnCancelledListener;

    /**
     * Constructor for listener on data change.
     * If changes were cancelled, writes message to user.
     *
     * @param onCancelledListener callback to invoke when the listener fails.
     */
    public AbstractOnFBDataChangeListener(
            final @NonNull AbstractDataAvailableListener onCancelledListener) {
        mOnCancelledListener = onCancelledListener;
        // TODO(dotdoom): consider mOnCancelledListener.setListener(this);
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
        mOnCancelledListener.onError(databaseError.toException());
    }
}
