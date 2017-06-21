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

import com.google.firebase.analytics.FirebaseAnalytics;

import org.dasfoo.delern.handlers.OnDeckViewHolderClick;
import org.dasfoo.delern.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.views.IDelernMainView;

public class DelernMainActivityPresenter implements OnDeckViewHolderClick {

    private IDelernMainView mDelernMainView;
    private AbstractDataAvailableListener mUserHasDecksListener;

    public DelernMainActivityPresenter(final IDelernMainView mDelernMainView) {
        this.mDelernMainView = mDelernMainView;
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
        // Checks if the recyclerview is empty, ProgressBar is invisible
        // and writes message for user
        // TODO(refactoring): new User();
        User user = new User();
        Deck.fetchCount(user.getChildReference(Deck.class).limitToFirst(1),
                mUserHasDecksListener);
    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    @Override
    public void doOnDeckClick(final int position) {
       // Deck deck = getDeckFromAdapter(position);
    }

    @Override
    public void doOnRenameMenuClick(final int position) {

    }

    @Override
    public void doOnEditMenuClick(final int position) {

    }

    @Override
    public void doOnDeleteMenuClick(final int position) {

    }

    @Override
    public void doOnDeckTypeClick(final int position, final DeckType deckType) {

    }

    public void cleanup() {
        mUserHasDecksListener.clean();
    }
}
