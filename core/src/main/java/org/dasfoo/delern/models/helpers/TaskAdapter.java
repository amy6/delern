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

package org.dasfoo.delern.models.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * TaskAdapter is a wrapper around firebase.Task and android.Task which both are functionally
 * equivalent but unfortunately reside in different packages.
 *
 * @param <T> Task result type.
 */
@SuppressWarnings({"checkstyle:IllegalCatch", "PMD.AvoidCatchingGenericException"})
public class TaskAdapter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAdapter.class);

    private final Queue<Consumer<T>> mOnResultQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Consumer<Exception>> mOnFailureQueue = new ConcurrentLinkedQueue<>();

    @SuppressWarnings("rawtypes")
    private TaskAdapter mDependentAdapter;

    /**
     * Syntax sugar for calling continueWith(..., true).
     *
     * @param continuation see continueWith().
     * @param <R>          see continueWith().
     * @return see continueWith().
     */
    public <R> TaskAdapter<R> continueWithOnce(
            final AbstractTrackingFunction<T, TaskAdapter<R>> continuation) {
        return continueWith(continuation, true);
    }

    /**
     * Call a function when this task is finished, return its TaskAdapter.
     *
     * @param continuation         a function to call after this task is finished.
     * @param stopAfterFirstResult whether to call stop() on this task when it's finished.
     * @param <R>                  result type for continuation.
     * @return TaskAdapter wrapping around continuation.
     */
    public <R> TaskAdapter<R> continueWith(
            final AbstractTrackingFunction<T, TaskAdapter<R>> continuation,
            final boolean stopAfterFirstResult) {
        final TaskAdapter<R> proxyAdapter = new TaskAdapter<>();
        onResult((final T parameter) -> {
            if (stopAfterFirstResult) {
                stop();
            }
            proxyAdapter.forwardFrom(continuation.call(parameter));
        });
        return proxyAdapter;
    }

    /**
     * Attach listeners to source and trigger this instance listeners whenever they fire.
     *
     * @param source TaskAdapter to forward listeners from.
     * @return this.
     */
    public TaskAdapter<T> forwardFrom(final TaskAdapter<T> source) {
        setDependentAdapter(source);
        source.onResult(this::triggerOnResult).onFailure(this::triggerOnFailure);
        return this;
    }

    /**
     * Trigger all onResult listeners. This method may have no effect in some subclasses that
     * implement a custom result triggering scheme.
     *
     * @param result result to be sent to listeners.
     */
    public void triggerOnResult(final T result) {
        for (Consumer<T> callback : mOnResultQueue) {
            try {
                callback.accept(result);
            } catch (Exception e) {
                LOGGER.error("Exception in onResult handler", e);
            }
        }
    }

    /**
     * Set up a listener for when the result is available (i.e. task has finished successfully).
     *
     * @param callback function to be called with task result.
     * @return this.
     */
    public TaskAdapter<T> onResult(final Consumer<T> callback) {
        mOnResultQueue.add(callback);
        return this;
    }

    protected void triggerOnFailure(final Exception error) {
        LOGGER.error("Error triggered for TaskAdapter", error);
        for (Consumer<Exception> callback : mOnFailureQueue) {
            try {
                callback.accept(error);
            } catch (Exception e) {
                LOGGER.error("Exception in onFailure handler", e);
            }
        }
    }

    /**
     * Set up a listener for task failure.
     *
     * @param callback function to be called with task error.
     * @return this.
     */
    public TaskAdapter<T> onFailure(final Consumer<Exception> callback) {
        mOnFailureQueue.add(callback);
        return this;
    }

    /**
     * Set an adapter that should be stopped when this adapter is stopped (forward stop()).
     *
     * @param dependent dependent adapter to be stopped.
     * @return this.
     */
    public TaskAdapter<T> setDependentAdapter(
            final @SuppressWarnings("rawtypes") TaskAdapter dependent) {
        mDependentAdapter = dependent;
        return this;
    }

    /**
     * Stop delivering any further triggered notifications, and potentially clean up resources.
     */
    public void stop() {
        if (mDependentAdapter != null) {
            mDependentAdapter.stop();
        }
    }
}
