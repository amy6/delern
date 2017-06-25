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

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.util.LogUtil;

/**
 * A listener creating an additional, AbstractModel-friendly layer of ValueEventListener callbacks.
 * @param <T> a type of the value to be passed into onData callback.
 */
public abstract class AbstractDataAvailableListener<T> {

    private static final String TAG = LogUtil.tagFor(AbstractDataAvailableListener.class);

    private final Context mContext;
    private Query mQuery;
    private ValueEventListener mListener;

    /**
     * Create a new listener.
     * @param context an Activity context for automatically displaying Toast to the user in case of
     *                failure.
     */
    public AbstractDataAvailableListener(@Nullable final Context context) {
        mContext = context;
    }

    /**
     * Callback invoked when data fetch has failed.
     * @param e error details if available, or null if no details are available.
     */
    public void onError(@Nullable final Exception e) {
        defaultOnError(e);
        if (mContext != null) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Callback invoked when data is available.
     * @param data a data based on where this callback is invoked from.
     */
    public abstract void onData(@Nullable T data);

    /**
     * Default error handler which logs the exception and sends the crash report.
     * @param e error details if available, or null .
     */
    public static void defaultOnError(@Nullable final Exception e) {
        Exception errorDetails = e;
        if (errorDetails == null) {
            // TODO(refactoring): add stack trace?
            errorDetails = new Exception("Unknown error");
        }
        Log.e(TAG, "Database operation failed:", errorDetails);
        Crashlytics.logException(errorDetails);
    }

    /**
     * For cleanup() only.
     * @param query query from which the listener should be removed (only useful with setListener).
     */
    public void setQuery(final Query query) {
        if (mQuery != null) {
            // TODO(refactoring): log to Log.e?
            Crashlytics.log(Log.ASSERT, TAG, "Query is overwritten");
        }
        mQuery = query;
    }

    /**
     * For cleanup() only.
     * @param listener listener to be removed from query (only useful in couple with setQuery).
     * @return listener.
     */
    public ValueEventListener setListener(final ValueEventListener listener) {
        if (mListener != null) {
            // TODO(refactoring): log to Log.e?
            Crashlytics.log(Log.ASSERT, TAG, "Listener is overwritten");
        }
        mListener = listener;
        return listener;
    }

    /**
     * Release resources and stop invoking this listener for future changes from now on.
     */
    public void cleanup() {
        if ((mQuery == null) != (mListener == null)) {
            // TODO(refactoring): log to Log.e?
            Crashlytics.log(Log.ASSERT, TAG, "Both Query and Listener must be set");
        }
        if (mQuery != null && mListener != null) {
            mQuery.removeEventListener(mListener);
            mQuery = null;
            mListener = null;
        }
    }
}
