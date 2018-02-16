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

import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

/**
 * A stub required by slf4j.
 */
@SuppressWarnings(/* naming required by slf4j */ "checkstyle:AbbreviationAsWordInName")
public final class StaticMDCBinder {

    /**
     * A stub for slf4j.
     */
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private StaticMDCBinder() {
    }

    /**
     * A stub for slf4j.
     *
     * @return the StaticMDCBinder singleton.
     */
    public static StaticMDCBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * A stub for slf4j.
     *
     * @return NOPMDCAdapter.
     */
    public MDCAdapter getMDCA() {
        return new NOPMDCAdapter();
    }

    /**
     * A stub for slf4j.
     *
     * @return NOPMDCAdapter class name.
     */
    public String getMDCAdapterClassStr() {
        return NOPMDCAdapter.class.getName();
    }
}
