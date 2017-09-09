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
import org.dasfoo.delern.models.DeckType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Presenter for EditDeckActivity. It performs logic with model
 */
public class EditDeckActivityPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditDeckActivityPresenter.class);

    private final IEditDeckView mView;
    private final Deck mDeck;

    /**
     * Constructor initialize presenter.
     *
     * @param view view for callbacks.
     * @param deck deck to change settings.
     */
    public EditDeckActivityPresenter(final IEditDeckView view, final Deck deck) {
        this.mView = view;
        this.mDeck = deck;
    }


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

    /**
     * Method choose new decktype for deck.
     *
     * @param position updated deck.
     */
    public void selectDeckType(final int position) {
        if (DeckType.values().length < position) {
            mView.showDeckTypeNotExistUserMessage();
            LOGGER.error("the selected item number is {}", position,
                    new IndexOutOfBoundsException());
            return;
        }
        if (position == -1 && mDeck.getDeckType() == null) {
            mDeck.setDeckType(DeckType.BASIC.name());
            return;
        }
        mDeck.setDeckType(DeckType.values()[position].name());
    }

    /**
     * Init decktype in Spinner. Checks that length of array in spinner equal to amount of
     * decktypes.
     *
     * @param arrayLength lenth array in spinner.
     * @return decktype.
     */
    // TODO(ksheremet): it should be covered by test
    public int setDefaultDeckType(final int arrayLength) {
        int deckTypeLength = DeckType.values().length;
        if (deckTypeLength > arrayLength) {
            LOGGER.error("DeckType has more types than Spinner", new IndexOutOfBoundsException());
            return DeckType.BASIC.ordinal();
        } else {
            return DeckType.valueOf(mDeck.getDeckType()).ordinal();
        }
    }

    /**
     * Change setting for deck for markdown.
     *
     * @param isMarkdown whether enable markdown or not.
     */
    public void setMarkdown(final boolean isMarkdown) {
        mDeck.setMarkdown(isMarkdown);
    }
}
