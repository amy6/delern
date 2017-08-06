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

/**
 * Class for a function of a single argument, with void return value.
 * Also keeps track of a caller, which is a TaskAdapter, propagating cleanup() to its stop().
 *
 * @param <T> function parameter type.
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractTrackingProcedure<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTrackingProcedure.class);

    private TaskAdapter mCaller;

    /**
     * Call a function.
     *
     * @param parameter function parameter.
     */
    public abstract void call(T parameter);

    /* default */ AbstractTrackingProcedure<T> setCaller(final TaskAdapter taskAdapter) {
        mCaller = taskAdapter;
        return this;
    }

    /**
     * Call stop() on a TaskAdapter, if set.
     */
    public void cleanup() {
        if (mCaller == null) {
            LOGGER.error("Attempt to call cleanup() without a caller!", new Exception("Traceback"));
            return;
        }
        mCaller.stop();
    }
}
