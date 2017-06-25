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
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by katarina on 3/7/17.
 * Listeners whether operation in Firebase was completed. If not, writes log message.
 */
public class OnFBOperationCompleteListener implements OnCompleteListener<Void>,
        DatabaseReference.CompletionListener {

    private static final OnFBOperationCompleteListener DEFAULT_INSTANCE =
            OnFBOperationCompleteListener.getDefaultInstance();

    private final AbstractDataAvailableListener mOnFailureListener;

    /**
     * Constructor.
     * @param onFailureListener callback which onError will be invoked on failure.
     */
    public OnFBOperationCompleteListener(
            final @Nullable AbstractDataAvailableListener onFailureListener) {
        mOnFailureListener = onFailureListener;
    }

    /**
     * {@inheritDoc}
     * Writes log on failure. Logic for success must be implemented in inherited class.
     */
    @Override
    public final void onComplete(@NonNull final Task task) {
        if (task.isSuccessful()) {
            onOperationSuccess();
        } else {
            if (mOnFailureListener == null) {
                AbstractDataAvailableListener.defaultOnError(task.getException());
                return;
            }
            mOnFailureListener.onError(task.getException());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete(final DatabaseError databaseError,
                           final DatabaseReference databaseReference) {
        if (databaseError == null) {
            onOperationSuccess();
        } else {
            if (mOnFailureListener == null) {
                AbstractDataAvailableListener.defaultOnError(databaseError.toException());
                return;
            }
            mOnFailureListener.onError(databaseError.toException());
        }
    }

    /**
     * Handles operation on success result. It has parameter that can be needed for the next
     * operations.
     */
    public void onOperationSuccess() {
        // Can be implemented in inherited class
    }

    /**
     * Default instance.
     * @return default instance.
     */
    public static OnFBOperationCompleteListener getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

}
