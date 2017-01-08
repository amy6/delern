package org.dasfoo.delern.util;

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
     * @param c class which methods will use the tag
     * @return tag name for Log.x() calls
     */
    public static String tagFor(final Class c) {
        return c.getSimpleName();
    }
}

