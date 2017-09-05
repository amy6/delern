package org.dasfoo.delern.util;

import java.util.Random;


/**
 * Generates random numbers for decks. It is needed that decks have unique names.
 */
public final class DeckPostfix {

    private static final DeckPostfix instance = new DeckPostfix();
    private static final Random rand = new Random();
    private static final int MAX = 10000;

    private DeckPostfix() {
    }

    public static int getRandomNumber() {
        return rand.nextInt(MAX) + 1;
    }
}
