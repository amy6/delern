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
import android.util.Log;

import org.dasfoo.delern.adapters.DeckRecyclerViewAdapter;
import org.dasfoo.delern.handlers.OnDeckViewHolderClick;
import org.dasfoo.delern.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.views.IDelernMainView;

public class DelernMainActivityPresenter implements OnDeckViewHolderClick {

    private static final String TAG = LogUtil.tagFor(DelernMainActivityPresenter.class);

    private IDelernMainView mDelernMainView;
    private AbstractDataAvailableListener mUserHasDecksListener;
    private AbstractDataAvailableListener<User> mAbstractDataAvailableListener;
    private DeckRecyclerViewAdapter mFirebaseAdapter;
    private User mUser;

    public DelernMainActivityPresenter(final IDelernMainView delernMainView) {
        this.mDelernMainView = delernMainView;
    }

    public void onCreate() {
        if (!User.isSignedIn()) {
            mDelernMainView.signIn();
        }
        mUserHasDecksListener = new AbstractDataAvailableListener<Long>(null) {

            @Override
            public void onData(@Nullable final Long isUserHasDecks) {
                mDelernMainView.hideProgressBar();
                if (isUserHasDecks == null || isUserHasDecks != 1) {
                    mDelernMainView.noDecksMessage(true);
                } else {
                    mDelernMainView.noDecksMessage(false);
                }
            }
        };
    }

    public void onStart() {
        if (mUser == null) {
            mUser = new User();
        }
        Deck.fetchCount(mUser.getChildReference(Deck.class).limitToFirst(1),
                mUserHasDecksListener);
    }

    public void onStop() {
        cleanup();
    }

    public DeckRecyclerViewAdapter getAdapter(final int layout) {
        if (mUser == null) {
            mUser = new User();
        }
        mFirebaseAdapter = new DeckRecyclerViewAdapter(layout, mUser.getChildReference(Deck.class));
        mFirebaseAdapter.setOnDeckViewHolderClick(this);
        return mFirebaseAdapter;
    }

    @Override
    public void learnDeck(final int position) {
        mDelernMainView.learnCardsInDeckClick(getDeckFromAdapter(position));
    }

    @Override
    public void renameDeck(final int position, final String newName) {
        Deck deck = getDeckFromAdapter(position);
        deck.setName(newName);
        deck.save(null);
    }

    @Override
    public void editDeck(final int position) {
        mDelernMainView.editCardsInDeckClick(getDeckFromAdapter(position));

    }

    @Override
    public void deleteDeck(final int position) {
        getDeckFromAdapter(position).delete();
    }

    @Override
    public void changeDeckType(final int position, final DeckType deckType) {
        Deck deck = getDeckFromAdapter(position);
        deck.setDeckType(deckType.name());
        deck.save(null);
    }

    private Deck getDeckFromAdapter(final int position) {
        return mFirebaseAdapter.getItem(position);
    }

    public void cleanup() {
        mUserHasDecksListener.cleanup();
        mFirebaseAdapter.cleanup();
        mAbstractDataAvailableListener.cleanup();
    }

    public void creteNewDeck(final String deckName) {
        final Deck newDeck = new Deck(new User());
        newDeck.setName(deckName);
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        newDeck.create(new AbstractDataAvailableListener<Deck>(null) {
            @Override
            public void onData(@Nullable final Deck deck) {
                mDelernMainView.editCardsInDeckClick(deck);
            }
        });
    }

    public void getUserInfo() {
        mAbstractDataAvailableListener = new AbstractDataAvailableListener<User>(null) {
            @Override
            public void onData(@Nullable final User user) {
                Log.d(TAG, "Check if user null");
                if (user == null) {
                    Log.d(TAG, "Starting sign in");
                    mDelernMainView.signIn();
                } else {
                    mUser = user;
                    mDelernMainView.updateUserProfileInfo(user);
                }
            }
        };

        User mUser = new User();
        mUser.fetchChild(mUser.getReference(), User.class, mAbstractDataAvailableListener, true);
    }
}
