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

import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.disposables.Disposable;


/**
 * Presenter for DelernMainActivity. It implements OnDeckAction to handle
 * user clicks. Class calls activity callbacks to show changed user data.
 */
public class DelernMainActivityPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelernMainActivityPresenter.class);

    private final IDelernMainView mDelernMainView;
    private Disposable mUserHasDecksListener;
    private Disposable mAbstractDataAvailableListener;
    private User mUser;

    /**
     * Constructor for DelernMainActivityPresenter. It gets DelernMainActivity view to perform
     * callbacks to Activity.
     *
     * @param delernMainView IDelernMainView for performing callbacks.
     */
    public DelernMainActivityPresenter(final IDelernMainView delernMainView) {
        this.mDelernMainView = delernMainView;
    }

    /**
     * Called from DelernMainActivity.onCreate(). Method checks whether user is signed in.
     * If not, it notifies DelernMainActivity that onCreate is not performed.
     * Method checks whether user has decks.
     *
     * @param user current user
     * @return whether onCreate() was performed or not.
     */
    public boolean onCreate(final User user) {
        if (user == null || !user.exists()) {
            LOGGER.debug("User is not Signed In");
            mDelernMainView.signIn();
            return false;
        }
        mUser = user;
        return true;
    }

    /**
     * Method is called in DelernMainActivity.onStart. It checks
     * whether user has decks or not.
     */
    public void onStart() {
        mUserHasDecksListener = Deck.fetchCount(mUser.getChildReference(Deck.class).limitToFirst(1))
                .subscribe((final Long isUserHasDecks) -> {
                    mDelernMainView.showProgressBar(false);
                    if (isUserHasDecks == null || isUserHasDecks != 1) {
                        mDelernMainView.noDecksMessage(true);
                    } else {
                        mDelernMainView.noDecksMessage(false);
                    }
                });
    }

    /**
     * Method is called in onStop in DelernMainActivity to release used resources.
     */
    public void onStop() {
        cleanup();
    }

    /**
     * Cleanup listeners and release resources.
     */
    public void cleanup() {
        // TODO(ksheremet): make these 2 one, by combining into a single disposable.
        if (mUserHasDecksListener != null) {
            mUserHasDecksListener.dispose();
        }
        if (mAbstractDataAvailableListener != null) {
            mAbstractDataAvailableListener.dispose();
        }
    }

    /**
     * Method creates new deck. It gets as parameter name of deck.
     *
     * @param deckName name of deck
     */
    public void createNewDeck(final String deckName) {
        final Deck newDeck = new Deck(mUser);
        newDeck.setName(deckName);
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        mDelernMainView.manageDisposable(newDeck.create().subscribe(
                () -> mDelernMainView.addCardsToDeck(newDeck)));
    }

    /**
     * Gets user data from FB Database. If user doesn't exist, calls sign in.
     * Otherwise calls callback method to update user profile info.
     */
    public void getUserInfo() {
        mAbstractDataAvailableListener = mUser.watch(User.class).subscribe((final User user) -> {
            LOGGER.debug("Check if user null");
            if (user == null) {
                LOGGER.debug("Starting sign in");
                mDelernMainView.signIn();
            } else {
                mUser = user;
                mDelernMainView.updateUserProfileInfo(user);
            }
        });
    }

    /**
     * Getter for user.
     *
     * @return user.
     */
    public User getUser() {
        return mUser;
    }
}
