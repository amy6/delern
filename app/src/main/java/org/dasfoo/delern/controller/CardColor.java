package org.dasfoo.delern.controller;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.DeckType;

import java.util.HashMap;

/**
 * Created by katarina on 2/26/17.
 * Class specifies color using gender.
 */

public final class CardColor {

    private static CardColor instance = null;

    private static HashMap<GrammaticalGenderSpecifier.Gender, Integer> color = new HashMap<>();

    private CardColor() {
        color.put(GrammaticalGenderSpecifier.Gender.FEMININE, R.color.feminine);
        color.put(GrammaticalGenderSpecifier.Gender.MASCULINE, R.color.masculine);
        color.put(GrammaticalGenderSpecifier.Gender.NEUTER, R.color.neuter);
        color.put(GrammaticalGenderSpecifier.Gender.NO_GENDER, R.color.noGender);
    }

    /**
     * Gets color specified for grammatical gender.
     *
     * @param gender grammatical gender.
     * @return color.
     */
    public static int getColor(GrammaticalGenderSpecifier.Gender gender) {
        if (instance == null) {
            instance = new CardColor();
        }
        return color.get(gender);
    }

}
