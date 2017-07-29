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

package org.dasfoo.delern.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple logger factory to create org.dasfoo.delern.logging.Logger for slf4j.
 */
@SuppressWarnings("PMD.MoreThanOneLogger")
public class LoggerFactory implements ILoggerFactory {
    private final ConcurrentHashMap<String, Logger> mLoggers = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getLogger(final String name) {
        Logger newLogger = new org.dasfoo.delern.logging.Logger(name);
        Logger oldLogger = mLoggers.putIfAbsent(name, newLogger);
        if (oldLogger == null) {
            return newLogger;
        }
        return oldLogger;
    }
}
