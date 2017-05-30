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

import org.dasfoo.delern.models.DeckType;

/**
 * Created by katarina on 2/17/17.
 * <p>
 * Class for specifying grammatical gender of a word.
 */
public final class GrammaticalGenderSpecifier {

    private GrammaticalGenderSpecifier() {
        // a private constructor to prevent instantiation
    }

    /**
     * It specifies gender of text. It currently can be SWISS or GERMAN for specifying gender.
     *
     * @param textType type of text.
     * @param text text of content.
     * @return gender of content.
     */
    public static Gender specifyGender(final DeckType textType, final String text) {
        if (text.contains(",")) {
            return Gender.NO_GENDER;
        }
        switch (textType) {
            case GERMAN:
                return specifyGermanGender(text);
            case SWISS:
                return specifySwissGender(text);
            default:
                return Gender.NO_GENDER;
        }
    }

    private static Gender specifyGermanGender(final String text) {
        if (text.startsWith("der ")) {
            return Gender.MASCULINE;
        }
        if (text.startsWith("die ")) {
            return Gender.FEMININE;
        }
        if (text.startsWith("das ")) {
            return Gender.NEUTER;
        }
        return Gender.NO_GENDER;
    }

    private static Gender specifySwissGender(final String text) {
        if (text.startsWith("de ")) {
            return Gender.MASCULINE;
        }
        if (text.startsWith("d ")) {
            return Gender.FEMININE;
        }
        if (text.startsWith("s ")) {
            return Gender.NEUTER;
        }
        return Gender.NO_GENDER;
    }

    /**
     * Contains possible genders for content.
     */
    public enum Gender {
        /**
         * Masculine gender of content.
         */
        MASCULINE,
        /**
         * Feminime gender of content.
         */
        FEMININE,
        /**
         * Neuter gender of content.
         */
        NEUTER,
        /**
         * No gender specified.
         */
        NO_GENDER,
    }
}
