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

import org.dasfoo.delern.models.DeckAccess;

/**
 * Created by katarina on 10/14/16.
 * Manages clicks on RecyclerView
 */

public interface OnDeckAction {

    /**
     * Manages click on deck.
     *
     * @param deckAccess deck information that user has
     */
    void learnDeck(DeckAccess deckAccess);

    /**
     * "Edit" menu item of a deck.
     *
     * @param deckAccess access to deck that user has
     */
    void editDeck(DeckAccess deckAccess);

    /**
     * Handles deck settings.
     *
     * @param deckAccess access to deck that user has
     */
    void editDeckSettings(DeckAccess deckAccess);

    /**
     * Handles sharing of the deck.
     *
     * @param deckAccess information about selected deck
     */
    void shareDeck(DeckAccess deckAccess);
}
