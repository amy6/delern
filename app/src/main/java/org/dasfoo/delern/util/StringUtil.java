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
import com.google.firebase.database.DatabaseReference;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by katarina on 5/15/17.
 */

public final class StringUtil {

    private static final String TAG = LogUtil.tagFor(StringUtil.class);

    private StringUtil() {

    }

    // TODO(refactoring): move into AbstractModel.

    /**
     * Get bare Firebase path (relative to the root) from the DatabaseReference.
     * @param reference DatabaseReference to get the path for.
     * @return toString() of the reference with protocol, hostname and port stripped.
     */
    public static String getFirebasePathFromReference(final DatabaseReference reference) {
        try {
            return new URI(reference.toString()).getPath();
        } catch (URISyntaxException e) {
            Crashlytics.logException(e);
            Log.e(TAG, "Cannot parse FBD Uri", e);
            // TODO(refactoring): make this all-writable for data recovery
            return "trash";
        }
    }
}
