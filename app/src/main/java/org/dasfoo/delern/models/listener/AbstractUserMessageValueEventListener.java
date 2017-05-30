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

package org.dasfoo.delern.models.listener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.util.LogUtil;

/**
 * Created by katarina on 3/1/17.
 * Abstract class that implement onCancel for all listeners.
 */
public abstract class AbstractUserMessageValueEventListener implements ValueEventListener {

    private static final String TAG = LogUtil.tagFor(AbstractUserMessageValueEventListener.class);

    private final Context mContext;

    /**
     * Constructor. Gets context to write message to user in case of error.
     *
     * @param context Context
     */
    public AbstractUserMessageValueEventListener(final Context context) {
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
        Log.e(TAG, "ValueEventListener cancelled [" + databaseError.getMessage() + "]: " +
                databaseError.getDetails());
        if (mContext != null) {
            Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Getter for context.
     *
     * @return context.
     */
    public Context getContext() {
        return mContext;
    }
}
