package org.dasfoo.delern.controller;

import org.dasfoo.delern.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by katarina on 2/26/17.
 * Class specifies color using gender.
 */

public final class CardColor {

    private static CardColor sInstance;

    private static Map<GrammaticalGenderSpecifier.Gender, Integer> sColor = new HashMap<>();

    private CardColor() {
        sColor.put(GrammaticalGenderSpecifier.Gender.FEMININE, R.color.feminine);
        sColor.put(GrammaticalGenderSpecifier.Gender.MASCULINE, R.color.masculine);
        sColor.put(GrammaticalGenderSpecifier.Gender.NEUTER, R.color.neuter);
        sColor.put(GrammaticalGenderSpecifier.Gender.NO_GENDER, R.color.noGender);
    }

    /**
     * Gets color specified for grammatical gender.
     *
     * @param gender grammatical gender.
     * @return color.
     */
    public static int getColor(final GrammaticalGenderSpecifier.Gender gender) {
        if (sInstance == null) {
            sInstance = new CardColor();
        }
        return sColor.get(gender);
    }

}
