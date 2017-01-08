package org.dasfoo.delern.models;

/**
 * Created by katarina on 11/1/16.
 */

public enum Level {
    L0,
    L1,
    L2,
    L3,
    L4,
    L5,
    L6,
    L7;

    private static Level[] sValues = values();

    public Level next() {
        return sValues[(this.ordinal() + 1) % sValues.length];
    }
}
