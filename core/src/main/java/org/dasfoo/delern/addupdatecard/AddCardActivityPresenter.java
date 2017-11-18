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

package org.dasfoo.delern.addupdatecard;

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;

/**
 * Presenter for AddEditCardActivity. It handles adding logic
 * and calls callbacks methods to update view for user.
 */
public class AddCardActivityPresenter implements IAddUpdatePresenter {

    private final IAddEditCardView mAddEditCardView;
    private final Deck mDeck;

    /**
     * Constructor for Presenter. It gets interface as parameter that implemented
     * in Activity to do callbacks.
     *
     * @param addEditCardView interface for performing callbacks.
     * @param deck            deck where to add cards.
     */
    public AddCardActivityPresenter(final IAddEditCardView addEditCardView, final Deck deck) {
        this.mAddEditCardView = addEditCardView;
        this.mDeck = deck;
    }

    /**
     * Method for adding card to FB.
     *
     * @param front text on front side of card.
     * @param back  text on back side of card.
     */
    private void add(final String front, final String back) {
        Card card = new Card(mDeck);
        card.setFront(front);
        card.setBack(back);
        mAddEditCardView.manageDisposable(card.create().subscribe(mAddEditCardView::cardAdded));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAddUpdate(final String front, final String back) {
        add(front, back);
        if (mAddEditCardView.addReversedCard()) {
            add(back, front);
        }
    }
}
