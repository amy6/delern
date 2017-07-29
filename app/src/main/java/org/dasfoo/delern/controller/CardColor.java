/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern.controller;

import org.dasfoo.delern.R;
import org.dasfoo.delern.presenters.helpers.GrammaticalGenderSpecifier;

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
