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

package org.dasfoo.delern.sharedeck;

import android.arch.lifecycle.LifecycleOwner;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;

import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.helpers.FirebaseSnapshotParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Places information about all users who can see and edit deck. Sets settings
 * for changing user's permissions.
 */
public class UserDeckAccessRecyclerViewAdapter
        extends FirebaseRecyclerAdapter<DeckAccess, UserDeckAccessViewHolder> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserDeckAccessRecyclerViewAdapter.class);

    private final ShareDeckActivityPresenter mPresenter;

    /**
     * Constructor for Adapter. Adapter places information about users, that can
     * use a deck.
     *
     * @param activity  Activity that manages this RecyclerView.
     * @param presenter presenter for performing operations.
     */
    public UserDeckAccessRecyclerViewAdapter(final LifecycleOwner activity,
                                             final ShareDeckActivityPresenter presenter) {
        super(new FirebaseRecyclerOptions.Builder<DeckAccess>()
                .setQuery(presenter.getReference(),
                        new FirebaseSnapshotParser<>(DeckAccess.class, presenter.getDeck()))
                .setLifecycleOwner(activity).build());
        this.mPresenter = presenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDeckAccessViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // TODO(dotdoom): design: either create an interface to pass instead of mPresenter, or
        //                pass presenter in everywhere else.
        return new UserDeckAccessViewHolder(parent, mPresenter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBindViewHolder(final UserDeckAccessViewHolder holder, final int position,
                                    final DeckAccess deckAccess) {
        holder.setDeckAccess(deckAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewRecycled(final UserDeckAccessViewHolder holder) {
        holder.setDeckAccess(null);
        super.onViewRecycled(holder);
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
