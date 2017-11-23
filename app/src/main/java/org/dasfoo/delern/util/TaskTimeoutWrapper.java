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

package org.dasfoo.delern.util;

import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A wrapper around Task which adds onCompleteListener for either task completion, or timeout.
 *
 * @param <T> Task result type.
 */
public class TaskTimeoutWrapper<T> {
    private final Task<T> mTask;
    private boolean mCompleted;

    /**
     * Create a new instance for a specific task.
     *
     * @param task Task which completion can be superseded by timeout.
     */
    public TaskTimeoutWrapper(final Task<T> task) {
        mTask = task;
    }

    /**
     * Set onComplete listener for the task or timeout. Can be called multiple times.
     *
     * @param timeoutMillis timeout for task completion in milliseconds.
     * @param onComplete    completion listener. Will be called only once.
     */
    public void onCompleteOrTimeout(final long timeoutMillis,
                                    final OnCompleteListener<T> onComplete) {
        final Runnable onTimeout = () -> {
            if (!mCompleted) {
                mCompleted = true;
                onComplete.onComplete(mTask);
            }
        };
        final Handler timeoutHandler = new Handler();
        timeoutHandler.postDelayed(onTimeout, timeoutMillis);

        mTask.addOnCompleteListener(task -> {
            if (!mCompleted) {
                mCompleted = true;
                timeoutHandler.removeCallbacks(onTimeout);
                onComplete.onComplete(mTask);
            }
        });
    }
}
