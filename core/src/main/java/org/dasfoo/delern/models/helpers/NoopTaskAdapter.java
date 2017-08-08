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

import java.util.function.Consumer;

/**
 * TaskAdapter for a fake task, when result is already available.
 *
 * @param <T> result type.
 */
public class NoopTaskAdapter<T> extends TaskAdapter<T> {
    private final T mResult;

    /**
     * Create a TaskAdapter, with any handlers being called immediately.
     *
     * @param result already available result.
     */
    public NoopTaskAdapter(final T result) {
        super();
        mResult = result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> TaskAdapter<R> continueWith(
            final AbstractTrackingFunction<T, TaskAdapter<R>> continuation,
            final boolean stopAfterFirstResult) {
        return continuation.call(mResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskAdapter<T> onResult(final Consumer<T> callback) {
        callback.accept(mResult);
        return this;
    }
}
