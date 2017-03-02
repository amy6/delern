package org.dasfoo.delern.controller;

import org.dasfoo.delern.R;
import org.dasfoo.delern.controller.CardColor;
import org.dasfoo.delern.controller.GrammaticalGenderSpecifier;
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
