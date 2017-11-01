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

import com.google.firebase.database.Query;

import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;


/**
 * Performs operation with a deck such as sharing, managing sharing permissions.
 */
public class ShareDeckActivityPresenter {

    private static final String READ_PERMISSION = "read";
    private static final String WRITE_PERMISSION = "write";

    private final Deck mDeck;

    /**
     * Constructor for presenter.
     *
     * @param deck current deck.
     */
    public ShareDeckActivityPresenter(final Deck deck) {
        this.mDeck = deck;
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
     * Returns position user access in a spinner to set.
     * 1 - can read.
     * 0 - can write.
     *
     * @param deckAccess deck access of a user
     * @return position in a spinner.
     */
    public int setUserAccessPositionForSpinner(final DeckAccess deckAccess) {
        if (READ_PERMISSION.equals(deckAccess.getAccess())) {
            return 1;
        }
        return 0;
    }

    /**
     * Reference to all users that use a deck.
     *
     * @return reference to all users that use a deck.
     */
    public Query getReference() {
        return mDeck.getChildReference(DeckAccess.class).orderByChild("access");
    }


    /**
     * Changes permissions for users that use decks.
     *
     * @param access     new permission.
     * @param deckAccess object for saving permissions.
     */
    public void changeUserPermission(final String access, final DeckAccess deckAccess) {
        deckAccess.setAccess(access);
        if ("".equals(access)) {
            deckAccess.delete();
            return;
        }
        if (WRITE_PERMISSION.equals(access) || READ_PERMISSION.equals(access)) {
            deckAccess.save();
        }
    }

    /**
     * Shares deck with user.
     *
     * @param uid    id of user.
     * @param access permissions for deck.
     */
    @SuppressWarnings("CheckReturnValue")
    public void shareDeck(final String uid, final String access) {
        DeckAccess deckAccess = new DeckAccess(mDeck);
        deckAccess.setKey(uid);
        deckAccess.setAccess(access);

        // TODO(ksheremet): write Deck for the new user.
        deckAccess.save();
    }
}
