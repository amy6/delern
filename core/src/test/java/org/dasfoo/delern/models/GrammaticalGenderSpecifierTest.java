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

package org.dasfoo.delern.models;

import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.presenters.helpers.GrammaticalGenderSpecifier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by katarina on 2/17/17.
 * <p>
 * Tests GrammaticalGenderSpecifier. Checks gender of sent content.
 */

public class GrammaticalGenderSpecifierTest {
    @Test
    public void feminineSwissSpecifier() throws Exception {
        assertEquals(GrammaticalGenderSpecifier.specifyGender(DeckType.SWISS, "d Frau"),
                GrammaticalGenderSpecifier.Gender.FEMININE);
    }

    @Test
    public void masculineSwissSpecifier() throws Exception {
        assertEquals(GrammaticalGenderSpecifier.specifyGender(DeckType.SWISS, "de Ma"),
                GrammaticalGenderSpecifier.Gender.MASCULINE);
    }

    @Test
    public void neuterSwissSpecifier() throws Exception {
        assertEquals(GrammaticalGenderSpecifier.specifyGender(DeckType.SWISS, "s Chind"),
                GrammaticalGenderSpecifier.Gender.NEUTER);
    }

    @Test
    public void noGenderSwissSpecifier() throws Exception {
        assertEquals(GrammaticalGenderSpecifier.specifyGender(DeckType.SWISS, "sich aazie"),
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
    }

    @Test
    public void feminineGermanSpecifier() throws Exception {
        assertEquals(GrammaticalGenderSpecifier.specifyGender(DeckType.GERMAN, "die Frau"),
                GrammaticalGenderSpecifier.Gender.FEMININE);
    }

    @Test
    public void masculineGermanSpecifier() throws Exception {
        assertEquals(GrammaticalGenderSpecifier.specifyGender(DeckType.GERMAN, "der Mann"),
                GrammaticalGenderSpecifier.Gender.MASCULINE);
    }

    @Test
    public void neuterGermanSpecifier() throws Exception {
        assertEquals(GrammaticalGenderSpecifier.specifyGender(DeckType.GERMAN, "das Kind"),
                GrammaticalGenderSpecifier.Gender.NEUTER);
    }

    @Test
    public void noGenderGermanSpecifier() throws Exception {
        assertEquals(GrammaticalGenderSpecifier.specifyGender(DeckType.GERMAN, "die Frau, der Man"),
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
    }
}
