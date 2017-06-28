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

package org.dasfoo.delern.presenters;

import android.support.annotation.Nullable;

import com.google.firebase.database.Query;

import org.dasfoo.delern.adapters.CardRecyclerViewAdapter;
import org.dasfoo.delern.handlers.OnCardViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.views.IEditCardListView;

/**
 * Presenter for EditCardListActivity. It performs operation with Model layer and
 * using callbacks update views of user.
 */
public class EditCardListActivityPresenter implements OnCardViewHolderClick {

    private final IEditCardListView mView;
    private Deck mDeck;
    private Query mQuery;
    private CardRecyclerViewAdapter mFirebaseAdapter;

    /**
     * Constructor for Presenter. It gets IEditCardListView as parameter
     * for performing callbacks to update view for user.
     *
     * @param view for performing callbacks.
     */
    public EditCardListActivityPresenter(final IEditCardListView view) {
        this.mView = view;
    }

    /**
     * Called from EditCardListActivity.onCreate(). It sets deck which
     * cards to show and gets Query to get list of cards from deck.
     *
     * @param deck deck which cards to show.
     */
    public void onCreate(final Deck deck) {
        mDeck = deck;
        mQuery = mDeck.getChildReference(Card.class);
    }

    /**
     * Called from EditCardListActivity to release used resources.
     */
    public void onStop() {
        mFirebaseAdapter.cleanup();
    }

    /**
     * Getter for deck.
     *
     * @return deck.
     */
    public Deck getDeck() {
        return mDeck;
    }

    private CardRecyclerViewAdapter createAdapter(@Nullable final Query query) {
        if (query == null) {
            mFirebaseAdapter = new CardRecyclerViewAdapter(mDeck, mQuery, this);
        } else {
            mFirebaseAdapter = new CardRecyclerViewAdapter(mDeck, query, this);
        }
        return mFirebaseAdapter;
    }

    /**
     * Getter method for CardRecyclerViewAdapter.
     *
     * @return CardRecyclerViewAdapter to display list of cards.
     */
    public CardRecyclerViewAdapter getAdapter() {
        return createAdapter(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCardClick(final int position) {
        mView.onCardPreview(mFirebaseAdapter.getItem(position));
    }

    /**
     * Called when user searches in list of cards.
     *
     * @param text text to be searched
     * @return Adapter with appropriate list of cards.
     */
    public CardRecyclerViewAdapter search(final String text) {
        return createAdapter(mQuery.orderByChild("front").startAt(text).endAt(text + "\uf8ff"));
    }
}
