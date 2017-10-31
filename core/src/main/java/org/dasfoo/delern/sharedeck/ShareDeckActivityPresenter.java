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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by katarina on 10/25/17.
 */

public class ShareDeckActivityPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareDeckActivityPresenter.class);
    private final IShareDeckView mView;
    private final Deck mDeck;

    public ShareDeckActivityPresenter(final IShareDeckView view, final Deck deck) {
        this.mView = view;
        this.mDeck = deck;
    }

    public Deck getDeck() {
        return mDeck;
    }

    public int getDefaultUserAccess(final DeckAccess deckAccess) {
        if ("read".equals(deckAccess.getAccess())) {
            return 1;
        }
        return 0;
    }

    public Query getReference() {
        return mDeck.getChildReference(DeckAccess.class).orderByChild("access");
    }


    /**
     * Changes permissions for users that use decks.
     *
     * @param access new permission.
     * @param deckAccess object for saving permissions.
     */
    public void changeUserPermission(final String access, final DeckAccess deckAccess) {
        deckAccess.setAccess(access);
        switch (access) {
            case "write":
                deckAccess.save();
                break;
            case "read":
                deckAccess.save();
                break;
            case "":
                deckAccess.delete();
                break;
            default:
                break;

        }
    }
}
