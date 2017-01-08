package org.dasfoo.delern.controller;

import org.dasfoo.delern.models.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by katarina on 11/6/16.
 */


public final class RepetitionIntervals {

    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static RepetitionIntervals ourInstance = new RepetitionIntervals();
    public Map<String, Long> intervals = new HashMap<>();

    private RepetitionIntervals() {
        intervals.put(Level.L0.name(), 4 * ONE_HOUR);
        intervals.put(Level.L1.name(), ONE_DAY);
        intervals.put(Level.L2.name(), 2 * ONE_DAY);
        intervals.put(Level.L3.name(), 5 * ONE_DAY);
        intervals.put(Level.L4.name(), 7 * ONE_DAY);
        intervals.put(Level.L5.name(), 14 * ONE_DAY);
        intervals.put(Level.L6.name(), 30 * ONE_DAY);
        intervals.put(Level.L7.name(), 60 * ONE_DAY);
    }

    public static RepetitionIntervals getInstance() {
        return ourInstance;
    }

    public static long getJitter() {
        // Random() method returns a random number between 0.0 and 0.999
        return (long) (Math.random() * 2 * ONE_HOUR + 1);
    }
}
