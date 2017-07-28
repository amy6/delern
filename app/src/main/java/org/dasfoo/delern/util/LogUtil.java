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

import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

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
     *
     * @param tag log tag.
     * @param msg log message.
     */
    public static void error(final String tag, final String msg) {
        error(tag, msg, null);
    }

    /**
     * Log error locally and to Crashlytics.
     *
     * @param tag log tag.
     * @param msg log message.
     * @param e   exception.
     */
    public static void error(final String tag, final String msg, @Nullable final Throwable e) {
        Log.e(tag, msg, e);
        Crashlytics.log(Log.ERROR, tag, msg);
        if (e != null) {
            Crashlytics.logException(e);
        }
    }

    /**
     * Add java.util.logging handler which logs errors to Android log and Crashlytics.
     */
    public static void addLogHandler() {
        LogManager.getLogManager().getLogger("").addHandler(new Handler() {
            @Override
            public void publish(final LogRecord record) {
                if (record.getLevel().intValue() >=
                        java.util.logging.Level.WARNING.intValue()) {
                    error(record.getLoggerName(), record.getMessage(),
                            record.getThrown());
                }
            }

            @Override
            public void flush() {
                // This method is not supported in Android or Crashlytics.
            }

            @Override
            public void close() {
                // This method is not supported in Android or Crashlytics.
            }
        });

    }
}

