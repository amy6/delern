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

import android.arch.lifecycle.LifecycleOwner;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;

import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.models.helpers.FirebaseSnapshotParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by katarina on 11/19/16.
 */

public class DeckRecyclerViewAdapter extends FirebaseRecyclerAdapter<Deck, DeckViewHolder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeckRecyclerViewAdapter.class);

    private final OnDeckAction mOnDeckAction;

    /**
     * Default constructor.
     *
     * @param user         current user.
     * @param activity     activity that manages this RecyclerView.
     * @param onDeckAction handler on recyclerview clicks.
     */
    public DeckRecyclerViewAdapter(final User user, final LifecycleOwner activity,
                                   final OnDeckAction onDeckAction) {
        super(new FirebaseRecyclerOptions.Builder<Deck>()
                .setQuery(user.getChildReference(Deck.class),
                        new FirebaseSnapshotParser<>(Deck.class, user))
                .setLifecycleOwner(activity).build());
        mOnDeckAction = onDeckAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeckViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new DeckViewHolder(parent, mOnDeckAction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBindViewHolder(final DeckViewHolder viewHolder, final int position,
                                    final Deck deck) {
        viewHolder.setDeck(deck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewRecycled(final DeckViewHolder viewHolder) {
        viewHolder.setDeck(null);
        super.onViewRecycled(viewHolder);
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
