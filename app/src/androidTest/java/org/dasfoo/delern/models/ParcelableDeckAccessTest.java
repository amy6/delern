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

import android.content.Intent;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests ParcelableDeckAccess
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ParcelableDeckAccessTest {
    private Deck mDeck;
    private DeckAccess mDeckAccess;

    @Before
    public void setParam() {
        User user = new User(null);
        user.setKey("user");
        user.setName("Bob");

        mDeck = new Deck(user);
        mDeck.setName("test");

        mDeckAccess = new DeckAccess(mDeck);
        mDeckAccess.setAccess("write");
    }

    @Test
    public void fetchParcelableDeckAccess() {
        String paramName = "deckAccess";
        Intent intent = new Intent();
        ParcelableDeckAccess parcelableDeckAccess = new ParcelableDeckAccess(mDeckAccess);
        intent.putExtra(paramName, parcelableDeckAccess);
        DeckAccess createdDeckAccess = ParcelableDeckAccess
                .get(intent.getParcelableExtra(paramName));
        assertEquals(mDeckAccess, createdDeckAccess);
    }

    @Test
    public void fetchDecksAndCompareNotEqual() {
        String paramName = "deckAccess";
        Intent intent = new Intent();
        ParcelableDeckAccess parcelableDeckAccess = new ParcelableDeckAccess(mDeckAccess);
        intent.putExtra(paramName, parcelableDeckAccess);
        DeckAccess fetchedDeckAccess = ParcelableDeckAccess.get(intent
                .getParcelableExtra(paramName));

        DeckAccess deckAccess2 = new DeckAccess(mDeck);
        deckAccess2.setAccess("read");

        Intent newIntent = new Intent();
        ParcelableDeckAccess newParcelableDeckAccess = new ParcelableDeckAccess(deckAccess2);
        newIntent.putExtra(paramName, newParcelableDeckAccess);
        DeckAccess newFetchedDeckAccess = ParcelableDeckAccess
                .get(newIntent.getParcelableExtra(paramName));
        assertNotEquals(fetchedDeckAccess, newFetchedDeckAccess);
    }
}
