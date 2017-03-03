package org.dasfoo.delern.controller;

import org.dasfoo.delern.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by katarina on 2/26/17.
 * Class specifies color using gender.
 */

public final class CardColor {

    private static final Map<GrammaticalGenderSpecifier.Gender, Integer> COLOR =
            new ConcurrentHashMap<>();

    // Initialize COLOR Map.
    static {
        COLOR.put(GrammaticalGenderSpecifier.Gender.FEMININE, R.color.feminine);
        COLOR.put(GrammaticalGenderSpecifier.Gender.MASCULINE, R.color.masculine);
        COLOR.put(GrammaticalGenderSpecifier.Gender.NEUTER, R.color.neuter);
        COLOR.put(GrammaticalGenderSpecifier.Gender.NO_GENDER, R.color.noGender);
    }

    private CardColor() {
        // Disable creating instance of class
    }

    /**
     * Gets color specified for grammatical gender.
     *
     * @param gender grammatical gender.
     * @return color.
     */
    public static int getColor(final GrammaticalGenderSpecifier.Gender gender) {
        return COLOR.get(gender);
    }
}
