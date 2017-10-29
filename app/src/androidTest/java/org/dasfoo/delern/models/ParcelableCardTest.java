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
 * Tests ParcelableCard
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ParcelableCardTest {

    private Deck mDeck;

    @Before
    public void setupParam() {
        User user = new User(null);
        user.setKey("user");
        user.setName("Bob");

        mDeck = new Deck(user);
        mDeck.setName("test");
    }

    @Test
    public void fetchParcelableUser() {
        String paramName = "card";
        Card card = new Card(mDeck);
        card.setFront("front");
        card.setBack("back");

        Intent intent = new Intent();
        ParcelableCard parcelableCard = new ParcelableCard(card);
        intent.putExtra(paramName, parcelableCard);
        Card fetchedCard = ParcelableCard.get(intent.getParcelableExtra(paramName));
        assertEquals(card, fetchedCard);
    }

    @Test
    public void fetchNotEqualCards() {
        String paramName = "card";
        Card card = new Card(mDeck);
        card.setFront("front");
        card.setBack("back");

        Intent intent = new Intent();
        ParcelableCard parcelableCard = new ParcelableCard(card);
        intent.putExtra(paramName, parcelableCard);
        Card fetchedCard = ParcelableCard.get(intent.getParcelableExtra(paramName));

        Card newCard = new Card(mDeck);

        Intent newIntent = new Intent();
        ParcelableCard newParcelableCard = new ParcelableCard(newCard);
        newIntent.putExtra(paramName, newParcelableCard);
        Card newFetchedCard = ParcelableCard.get(newIntent.getParcelableExtra(paramName));

        assertNotEquals(fetchedCard, newFetchedCard);
    }
}
