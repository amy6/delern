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

import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.helpers.FirebaseSnapshotParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activity displays list of cards in given deck.
 */
public class CardRecyclerViewAdapter extends FirebaseRecyclerAdapter<Card, CardViewHolder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardRecyclerViewAdapter.class);

    private final OnCardViewHolderClick mOnCardViewHolderClick;

    /**
     * Create a new FirebaseRecyclerAdapter.
     *
     * @param deck     deck which cards to show.
     * @param query    reference to FB to cards of deck.
     * @param listener listener to handle clicks on card.
     */
    public CardRecyclerViewAdapter(final Deck deck, final Query query,
                                   final OnCardViewHolderClick listener) {
        super(new FirebaseRecyclerOptions.Builder<Card>()
                .setQuery(query, new FirebaseSnapshotParser<>(Card.class, deck)).build());
        this.mOnCardViewHolderClick = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new CardViewHolder(parent, mOnCardViewHolderClick);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBindViewHolder(final CardViewHolder viewHolder, final int position,
                                    final Card card) {
        viewHolder.setCard(card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(final DatabaseError error) {
        LOGGER.error("Error in Adapter: ", error.toException());
        super.onError(error);
    }
}
