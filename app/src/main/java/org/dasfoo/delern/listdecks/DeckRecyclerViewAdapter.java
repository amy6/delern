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

package org.dasfoo.delern.listdecks;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.models.helpers.FirebaseSnapshotParser;

/**
 * Created by katarina on 11/19/16.
 */

public class DeckRecyclerViewAdapter extends FirebaseRecyclerAdapter<Deck, DeckViewHolder> {

    private static final int CARDS_COUNTER_LIMIT = 200;
    private OnDeckViewHolderClick mOnDeckViewHolderClick;

    /**
     * Default constructor.
     *
     * @param modelLayout This is the layout used to represent a single item in the list.
     *                    You will be responsible for populating an instance of the
     *                    corresponding view with the data from an instance of modelClass.
     * @param user        Current user.
     */
    public DeckRecyclerViewAdapter(final int modelLayout, final User user) {
        super(new FirebaseSnapshotParser<>(Deck.class, user),
                modelLayout, DeckViewHolder.class, user.getChildReference(Deck.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("CheckReturnValue")
    protected void populateViewHolder(final DeckViewHolder viewHolder, final Deck deck,
                                      final int position) {
        viewHolder.mDeckTextView.setText(deck.getName());
        deck.fetchDeckAccessOfUser().subscribe((final DeckAccess deckAccess) -> {
            viewHolder.mDeckAccess = deckAccess;
        });

        Deck.fetchCount(
                getItem(position).fetchCardsToRepeatWithLimitQuery(CARDS_COUNTER_LIMIT + 1))
                .subscribe((final Long cardsCount) -> {
                    if (cardsCount <= CARDS_COUNTER_LIMIT) {
                        viewHolder.mCountToLearnTextView.setText(
                                String.valueOf(cardsCount));
                    } else {
                        String tooManyCards = CARDS_COUNTER_LIMIT + "+";
                        viewHolder.mCountToLearnTextView.setText(tooManyCards);
                    }
                });
        viewHolder.setOnViewClick(mOnDeckViewHolderClick);
    }

    /**
     * Sets on Deck menu clicks handler.
     *
     * @param onDeckViewHolderClick handler on recyclerview clicks
     */
    public void setOnDeckViewHolderClick(final OnDeckViewHolderClick onDeckViewHolderClick) {
        this.mOnDeckViewHolderClick = onDeckViewHolderClick;
    }
}
