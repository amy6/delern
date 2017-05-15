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
