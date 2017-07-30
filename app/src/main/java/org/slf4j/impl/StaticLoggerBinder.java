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

    /**
     * Declare the version of the SLF4J API this implementation is compiled
     * against. The value of this field is usually modified with each release.
     */
    // to avoid constant folding by the compiler, this field must *not* be final
    @SuppressWarnings({
            "checkstyle:StaticVariableName", "checkstyle:VisibilityModifier",
            "PMD.VariableNamingConventions", "PMD.SuspiciousConstantFieldName",
            "PMD.VariableNamingConventions"})
    public static String REQUESTED_API_VERSION = "1.7.25";

    private static final ILoggerFactory LOGGER_FACTORY = new LoggerFactory();
    private static final String LOGGER_FACTORY_STR = LOGGER_FACTORY.getClass().getName();
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /**
     * Return the singleton of this class.
     * This method is not in the interface, but is required by slf4j.
     *
     * @return the StaticLoggerBinder singleton.
     */
    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

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
