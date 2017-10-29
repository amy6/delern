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
 * Tests ParcelableDeck
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ParcelableDeckTest {

    private User mUser;
    private Deck mDeck;

    @Before
    public void setParam() {
        mUser = new User(null);
        mUser.setKey("user");
        mUser.setName("Bob");

        mDeck = new Deck(mUser);
        mDeck.setName("test");
    }

    @Test
    public void fetchParcelableDeck() {
        String paramName = "deck";
        Intent intent = new Intent();
        ParcelableDeck parcelableDeck = new ParcelableDeck(mDeck);
        intent.putExtra(paramName, parcelableDeck);
        Deck createdDeck = ParcelableDeck.get(intent.getParcelableExtra(paramName));
        assertEquals(mDeck, createdDeck);
    }

    @Test
    public void fetchDecksAndCompareNotEqual() {
        String paramName = "deck";
        Intent intent = new Intent();
        ParcelableDeck parcelableDeck = new ParcelableDeck(mDeck);
        intent.putExtra(paramName, parcelableDeck);
        Deck fetchedDeck = ParcelableDeck.get(intent.getParcelableExtra(paramName));


        Deck deck2 = new Deck(mUser);
        deck2.setName("test2");

        Intent newIntent = new Intent();
        ParcelableDeck newParcelableDeck = new ParcelableDeck(deck2);
        newIntent.putExtra(paramName, newParcelableDeck);
        Deck newFetchedDeck = ParcelableDeck.get(newIntent.getParcelableExtra(paramName));
        assertNotEquals(fetchedDeck, newFetchedDeck);
    }
}
