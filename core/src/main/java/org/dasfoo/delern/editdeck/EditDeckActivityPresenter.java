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

package org.dasfoo.delern.editdeck;

import org.dasfoo.delern.models.Deck;

/**
 * Presenter for EditDeckActivity. It performs logic with model
 */
public class EditDeckActivityPresenter {

    /**
     * Method deletes deck.
     *
     * @param deck deck to delete.
     */
    public void deleteDeck(final Deck deck) {
        deck.delete();
    }

    /**
     * Method renames deck or changes type of deck.
     *
     * @param newDeck updated deck.
     */
    public void updateDeck(final Deck newDeck) {
        newDeck.save();
    }
}
