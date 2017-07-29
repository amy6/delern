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

package org.slf4j.impl;

import org.dasfoo.delern.logging.LoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * A simple logger binder to org.dasfoo.delern.logging.LoggerFactory for slf4j.
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {

    private static final ILoggerFactory LOGGER_FACTORY = new LoggerFactory();
    private static final String LOGGER_FACTORY_STR = LOGGER_FACTORY.getClass().getName();

    /**
     * {@inheritDoc}
     */
    @Override
    public ILoggerFactory getLoggerFactory() {
        return LOGGER_FACTORY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLoggerFactoryClassStr() {
        return LOGGER_FACTORY_STR;
    }
}
