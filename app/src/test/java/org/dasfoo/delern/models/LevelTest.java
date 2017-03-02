package org.dasfoo.delern.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by katarina on 3/2/17.
 * Tests method in Level class for getting next level using current.
 */

public class LevelTest {

    @Test
    public void getNextLevelForL0() {
        assertEquals(Level.getNextLevel(Level.L0.name()), Level.L1.name());
    }

    @Test
    public void getNextLevelForL1() {
        assertEquals(Level.getNextLevel(Level.L1.name()), Level.L2.name());
    }

    @Test
    public void getNextLevelForL2() {
        assertEquals(Level.getNextLevel(Level.L2.name()), Level.L3.name());
    }

    @Test
    public void getNextLevelForL3() {
        assertEquals(Level.getNextLevel(Level.L3.name()), Level.L4.name());
    }

    @Test
    public void getNextLevelForL4() {
        assertEquals(Level.getNextLevel(Level.L4.name()), Level.L5.name());
    }

    @Test
    public void getNextLevelForL5() {
        assertEquals(Level.getNextLevel(Level.L5.name()), Level.L6.name());
    }

    @Test
    public void getNextLevelForL6() {
        assertEquals(Level.getNextLevel(Level.L6.name()), Level.L7.name());
    }

    @Test
    public void getNextLevelForL7() {
        assertEquals(Level.getNextLevel(Level.L7.name()), Level.L7.name());
    }
}
