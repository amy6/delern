package org.dasfoo.delern;

import org.dasfoo.delern.controller.GrammaticalGenderSpecifier;
import org.dasfoo.delern.models.DeckType;
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
