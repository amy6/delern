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

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.util.LogUtil;

/**
 * A listener creating an additional, AbstractModel-friendly layer of ValueEventListener callbacks.
 *
 * @param <T> a type of the value to be passed into onData callback.
 */
public abstract class AbstractDataAvailableListener<T> {

    private static final String TAG = LogUtil.tagFor(AbstractDataAvailableListener.class);

    private Query mQuery;
    private ValueEventListener mListener;

    // TODO(dotdoom): create a wrapper around AbstractDataAvailableListener that would show a
    //                toast on error.

    /**
     * Default error handler which logs the exception and sends the crash report.
     *
     * @param e error details if available, or null.
     */
    public static void defaultOnError(@Nullable final Exception e) {
        Exception errorDetails = e;
        if (errorDetails == null) {
            errorDetails = new Exception("Unknown error");
        }
        LogUtil.error(TAG, "Database operation failed", errorDetails);
    }

    /**
     * Callback invoked when data fetch has failed.
     *
     * @param e error details if available, or null if no details are available.
     */
    public void onError(@Nullable final Exception e) {
        defaultOnError(e);
    }

    /**
     * Callback invoked when data is available.
     *
     * @param data a data based on where this callback is invoked from.
     */
    public abstract void onData(@Nullable T data);

    /**
     * For cleanup() only.
     *
     * @param query    query from which the listener should be removed (only good with setListener).
     * @param listener listener to be removed from query (only useful in couple with setQuery).
     * @return listener.
     */
    public ValueEventListener setCleanupPair(@NonNull final Query query,
                                             @NonNull final ValueEventListener listener) {
        // TODO(dotdoom): consider creating an attach() method instead.
        if (mQuery != null) {
            LogUtil.error(TAG, "Data Available Listener cleanup pair is overwritten");
        }
        mQuery = query;
        mListener = listener;
        return listener;
    }

    /**
     * Release resources and stop invoking this listener for future changes from now on.
     */
    public void cleanup() {
        if (mQuery != null && mListener != null) {
            mQuery.removeEventListener(mListener);
            mQuery = null;
            mListener = null;
        }
    }
}
