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

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.presenters.interfaces.IAddUpdatePresenter;
import org.dasfoo.delern.views.IAddEditCardView;

/**
 * Presenter for AddEditCardActivity. It handles adding logic
 * and calls callbacks methods to update view for user.
 */
public class AddCardActivityPresenter implements IAddUpdatePresenter {

    private final IAddEditCardView mAddEditCardView;
    private final Card mCard;

    /**
     * Constructor for Presenter. It gets interface as parameter that implemented
     * in Activity to do callbacks.
     *
     * @param addEditCardView interface for performing callbacks.
     * @param card            card for updating or empty for adding.
     */
    public AddCardActivityPresenter(final IAddEditCardView addEditCardView, final Card card) {
        this.mAddEditCardView = addEditCardView;
        this.mCard = card;
    }

    /**
     * OnCreate method that called from AddEditCardActivity.onCreate.
     * It checks whether user is going to add new cards or update existing cards.
     * It calls callback method in AddEditCardActivity to initialize views accordingly.
     */
    @Override
    public void onCreate() {
        mAddEditCardView.initForAdd();
    }

    /**
     * Method for adding card to FB.
     *
     * @param front text on front side of card.
     * @param back  text on back side of card.
     */
    @SuppressWarnings("CheckReturnValue")
    private void add(final String front, final String back) {
        mCard.create(front, back).subscribe(mAddEditCardView::cardAdded);
    }

    /**
     * Performs when user wants to add or update cards.
     *
     * @param front front side of card.
     * @param back  back side of card.
     */
    @Override
    public void onAddUpdate(final String front, final String back) {
        add(front, back);
        if (mAddEditCardView.addReversedCard()) {
            add(back, front);
        }
    }
}
