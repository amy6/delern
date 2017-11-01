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

public interface OnDeckViewHolderClick {

    /**
     * Manages click on deck.
     *
     * @param position position of the clicked element in the list
     */
    void learnDeck(int position);

    /**
     * "Edit" menu item of a deck.
     *
     * @param position position of the element in the list
     */
    void editDeck(int position);

    /**
     * Handles deck settings.
     *
     * @param deckAccess position of the element in the list
     */
    void editDeckSettings(DeckAccess deckAccess);

    /**
     * Handles sharing of the deck.
     *
     * @param position position of the element in the list
     */
    void shareDeck(int position);
}
