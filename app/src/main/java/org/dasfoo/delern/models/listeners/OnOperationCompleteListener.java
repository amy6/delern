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

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by katarina on 3/7/17.
 * Listeners whether operation in Firebase was completed. If not, writes log message.
 */
@SuppressWarnings("rawtypes")
public class OnOperationCompleteListener implements OnCompleteListener<Void>,
        DatabaseReference.CompletionListener {

    // We don't store context in this instance (it's always null).
    @SuppressLint("StaticFieldLeak")
    private static final OnOperationCompleteListener DEFAULT_INSTANCE =
            new OnOperationCompleteListener();

    private Context mContext;

    /**
     * Create a listener with onError implementation showing a Toast with context.
     *
     * @param context used to show a Toast.
     */
    // TODO(dotdoom): replace context with some IUserNotifier.
    public OnOperationCompleteListener(@NonNull final Context context) {
        mContext = context;
    }

    /**
     * Create a listener with default onError implementation (see AbstractDataAvailableListener).
     */
    public OnOperationCompleteListener() {
        // Intentionally left blank.
    }

    /**
     * Default instance.
     *
     * @return default instance.
     */
    public static OnOperationCompleteListener getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /**
     * {@inheritDoc}
     * Writes log on failure. Logic for success must be implemented in inherited class.
     */
    @Override
    public final void onComplete(@NonNull final Task task) {
        if (task.isSuccessful()) {
            onSuccess();
        } else {
            AbstractDataAvailableListener.defaultOnError(task.getException(), mContext);
        }
    }

    /**
     * {@inheritDoc}
     * Writes log on failure. Logic for success must be implemented in inherited class.
     */
    @Override
    public final void onComplete(final DatabaseError databaseError,
                                 final DatabaseReference databaseReference) {
        if (databaseError == null) {
            onSuccess();
        } else {
            AbstractDataAvailableListener.defaultOnError(databaseError.toException(), mContext);
        }
    }

    /**
     * Handles operation on success result. It has parameter that can be needed for the next
     * operations.
     */
    public void onSuccess() {
        // Can be implemented in inherited class
    }

}
