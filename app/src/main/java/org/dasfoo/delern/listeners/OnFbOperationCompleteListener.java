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
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by katarina on 3/7/17.
 * Listeners whether operation in Firebase was completed. If not, writes log message.
 */
public class OnFbOperationCompleteListener implements OnCompleteListener<Void> {

    private final String mTag;
    private final Context mContext;

    /**
     * Tag for logging. It describes from what class was called.
     *
     * @param tag     tag for logging.
     * @param context context for writing user message.
     */
    public OnFbOperationCompleteListener(final String tag, final Context context) {
        this.mTag = tag;
        this.mContext = context;
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
            String message = "Firebase operation is not completed:";
            Log.e(mTag, message, task.getException());
            Crashlytics.log(Log.ERROR, mTag + message,
                    task.getException().toString());
            Toast.makeText(mContext, task.getException().getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles operation on success result. It has parameter that can be needed for the next
     * operations.
     */
    public void onOperationSuccess() {
        // Can be implemented in inherited class
    }
}
