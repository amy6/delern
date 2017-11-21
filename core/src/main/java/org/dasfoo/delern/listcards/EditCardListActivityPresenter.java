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

package org.dasfoo.delern.listcards;

import com.google.firebase.database.Query;

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.util.GrammaticalGenderSpecifier;

/**
 * Presenter for EditCardListActivity. It performs operation with Model layer and
 * using callbacks update views of user.
 */
public class EditCardListActivityPresenter {

    private final Deck mDeck;
    private final Query mQuery;

    /**
     * It sets deck which cards to show and gets Query to get list of cards from deck.
     *
     * @param deck deck which cards to show.
     */
    public EditCardListActivityPresenter(final Deck deck) {
        this.mDeck = deck;
        mQuery = mDeck.getChildReference(Card.class);
    }

    /**
     * Getter for deck.
     *
     * @return deck.
     */
    public Deck getDeck() {
        return mDeck;
    }

    /**
     * Getter for Query (query of cards).
     *
     * @return query of cards.
     */
    public Query getQuery() {
        return mQuery;
    }

    /**
     * Specifies grammatical gender of content.
     *
     * @param backSide back side of card.
     * @return gender of content.
     */
    public GrammaticalGenderSpecifier.Gender specifyContentGender(final String backSide) {
        return GrammaticalGenderSpecifier.specifyGender(
                    DeckType.valueOf(mDeck.getDeckType()),
                    backSide);
    }
}
