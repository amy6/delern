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
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test CardColor for specifying background color of card.
 */

public class CardColorTest {
    @Test
    public void feminineCardColor() {
        Assert.assertEquals(CardColor
                .getColor(GrammaticalGenderSpecifier.Gender.FEMININE), R.color.feminine);
    }

    @Test
    public void masculineCardColor() {
        assertEquals(CardColor
                .getColor(GrammaticalGenderSpecifier.Gender.MASCULINE), R.color.masculine);
    }

    @Test
    public void neuterCardColor() {
        assertEquals(CardColor.getColor(GrammaticalGenderSpecifier.Gender.NEUTER), R.color.neuter);
    }

    @Test
    public void noGenderCardColor() {
        assertEquals(CardColor
                .getColor(GrammaticalGenderSpecifier.Gender.NO_GENDER), R.color.noGender);
    }
}
