package org.dasfoo.delern.controller;

import org.dasfoo.delern.models.Level;

import java.util.HashMap;

/**
 * Created by katarina on 11/6/16.
 */
public class RepetitionIntervals {
    private static RepetitionIntervals ourInstance = new RepetitionIntervals();
    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public static RepetitionIntervals getInstance() {
        return ourInstance;
    }

    public HashMap<String, Long> intervals = new HashMap<>();

    private RepetitionIntervals() {
        intervals.put(Level.L1.name(), 1 * ONE_DAY);
        intervals.put(Level.L2.name(), 2 * ONE_DAY);
        intervals.put(Level.L3.name(), 5 * ONE_DAY);
        intervals.put(Level.L4.name(), 7 * ONE_DAY);
        intervals.put(Level.L5.name(), 14 * ONE_DAY);
        intervals.put(Level.L6.name(), 30 * ONE_DAY);
        intervals.put(Level.L7.name(), 60 * ONE_DAY);
    }
}
