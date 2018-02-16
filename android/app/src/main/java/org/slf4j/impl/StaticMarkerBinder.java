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

import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * A stub required by slf4j.
 */
public final class StaticMarkerBinder implements MarkerFactoryBinder {

    /**
     * A stub for slf4j.
     */
    public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

    private final IMarkerFactory mMarkerFactory = new BasicMarkerFactory();

    private StaticMarkerBinder() {
    }

    /**
     * Return the singleton of this class.
     * This method is not in the interface, but is required by slf4j.
     *
     * @return the StaticMarkerBinder singleton.
     */
    public static StaticMarkerBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMarkerFactory getMarkerFactory() {
        return mMarkerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
}
