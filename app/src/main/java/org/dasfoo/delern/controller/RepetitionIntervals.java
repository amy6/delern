package org.dasfoo.delern.controller;

import org.dasfoo.delern.models.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by katarina on 11/6/16.
 * Class implements repetition intervals for card.
 */
public final class RepetitionIntervals {

    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static RepetitionIntervals sOurInstance = new RepetitionIntervals();
    private final Map<String, Long> mIntervals = new ConcurrentHashMap<>();

    @SuppressWarnings("checkstyle:MagicNumber")
    private RepetitionIntervals() {
        mIntervals.put(Level.L0.name(), 4 * ONE_HOUR);
        mIntervals.put(Level.L1.name(), ONE_DAY);
        mIntervals.put(Level.L2.name(), 2 * ONE_DAY);
        mIntervals.put(Level.L3.name(), 5 * ONE_DAY);
        mIntervals.put(Level.L4.name(), 7 * ONE_DAY);
        mIntervals.put(Level.L5.name(), 14 * ONE_DAY);
        mIntervals.put(Level.L6.name(), 30 * ONE_DAY);
        mIntervals.put(Level.L7.name(), 60 * ONE_DAY);
    }

    /**
     * Class is singleton. This method is used to return instance of class.
     *
     * @return instance of this class
     */
    public static RepetitionIntervals getInstance() {
        return sOurInstance;
    }

    /**
     * Get jitter of the card for randomizing appearance.
     *
     * @return period of time in milliseconds.
     */
    private long getJitter() {
        // Random() method returns a random number between 0.0 and 0.999
        return (long) (Math.random() * 2 * ONE_HOUR + 1);
    }

    /**
     * Counts next time to repeat.
     *
     * @param level current level
     * @return next time to repeat
     */
    public long getNextTimeToRepeat(final String level) {
        return System.currentTimeMillis() + getInstance().getInterval(level) + getJitter();
    }

    /**
     * Get interval of the next card appearance for the level.
     *
     * @param levelName name of the level, one of Level.* constants
     * @return interval in milliseconds
     */
    private Long getInterval(final String levelName) {
        return mIntervals.get(levelName);
    }
}
