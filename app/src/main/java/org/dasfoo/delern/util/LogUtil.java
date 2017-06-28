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

import android.util.Log;

import com.crashlytics.android.Crashlytics;

/**
 * Helper methods for Android Logging.
 */
public final class LogUtil {
    /**
     * Hide utility class default constructor.
     */
    private LogUtil() {
    }

    /**
     * Class information for logging.
     *
     * @param c class which methods will use the tag
     * @return tag name for Log.x() calls
     */
    @SuppressWarnings("rawtypes")
    public static String tagFor(final Class c) {
        return c.getSimpleName();
    }

    /**
     * Log error locally and to Crashlytics.
     * @param tag log tag.
     * @param msg log message.
     */
    public static void error(final String tag, final String msg) {
        Log.e(tag, msg);
        Crashlytics.log(Log.ERROR, tag, msg);
    }

    /**
     * Log error locally and to Crashlytics.
     * @param tag log tag.
     * @param msg log message.
     * @param e   exception.
     */
    public static void error(final String tag, final String msg, final Throwable e) {
        Log.e(tag, msg, e);
        Crashlytics.log(Log.ERROR, tag, msg);
        Crashlytics.logException(e);
    }
}

