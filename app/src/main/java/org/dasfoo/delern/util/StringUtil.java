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

import android.text.TextUtils;

/**
 * Created by katarina on 5/15/17.
 */

public final class StringUtil {

    private static final String FIREBASE_NODE_DELIMITER = "/";

    private StringUtil() {

    }

    /**
     * Joins Firebase children to create Firebase node path.
     *
     * @param children children of Firebase.
     * @return path in Firebase.
     */
    public static String joinFirebasePath(final String... children) {
        return TextUtils.join(FIREBASE_NODE_DELIMITER, children);
    }
}
