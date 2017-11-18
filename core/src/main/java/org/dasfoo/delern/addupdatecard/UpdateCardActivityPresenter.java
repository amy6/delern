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

/**
 * Presenter for AddEditCardActivity. It handles updating card logic
 * and calls callbacks methods to update view for user.
 * TODO(ksheremet): split into 2 different classes; perhaps even 2 interfaces for a view
 * (can still be a single Activity implementing 2 interfaces)
 */
public class UpdateCardActivityPresenter implements IAddUpdatePresenter {

    private final IAddEditCardView mAddEditCardView;
    private final Card mCard;

    /**
     * Constructor for Presenter. It gets interface as parameter that implemented
     * in Activity to do callbacks.
     *
     * @param addEditCardView interface for performing callbacks.
     * @param card            card for updating or empty for adding.
     */
    public UpdateCardActivityPresenter(final IAddEditCardView addEditCardView, final Card card) {
        this.mAddEditCardView = addEditCardView;
        this.mCard = card;
    }

    /**
     * Method updates existing card in FB.
     *
     * @param newFront new front side of card.
     * @param newBack  new back side of card.
     */
    private void update(final String newFront, final String newBack) {
        mCard.setFront(newFront);
        mCard.setBack(newBack);
        mAddEditCardView.manageDisposable(mCard.save().subscribe(mAddEditCardView::cardUpdated));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAddUpdate(final String front, final String back) {
        update(front, back);
    }
}
