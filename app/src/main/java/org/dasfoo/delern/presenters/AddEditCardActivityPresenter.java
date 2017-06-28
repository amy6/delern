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
import org.dasfoo.delern.models.Level;
import org.dasfoo.delern.models.ScheduledCard;
import org.dasfoo.delern.models.helpers.MultiWrite;
import org.dasfoo.delern.models.listeners.OnOperationCompleteListener;
import org.dasfoo.delern.views.IAddEditCardView;

/**
 * Presenter for AddEditCardActivity. It handles adding and updating card logic
 * and calls callbacks methods to update view for user.
 */
public class AddEditCardActivityPresenter {

    private final IAddEditCardView mAddEditCardView;
    private Card mCard;

    /**
     * Constructor for Presenter. It gets interface as parameter that implemented
     * in Activity to do callbacks.
     *
     * @param addEditCardView interface for performing callbacks.
     */
    public AddEditCardActivityPresenter(final IAddEditCardView addEditCardView) {
        this.mAddEditCardView = addEditCardView;
    }

    /**
     * OnCreate method that called from AddEditCardActivity.onCreate.
     * It checks whether user is going to add new cards or update existing cards.
     * It calls callback method in AddEditCardActivity to initialize views accordingly.
     *
     * @param card new card for adding or existing for updating.
     */
    public void onCreate(final Card card) {
        mCard = card;
        if (mCard.exists()) {
            mAddEditCardView.initForUpdate(mCard.getFront(), mCard.getBack());
        } else {
            mAddEditCardView.initForAdd();
        }
    }

    /**
     * Method sets front and back sides of card to null. It is needed
     * for adding new cards.
     */
    public void cleanCardFields() {
        mCard.setFront(null);
        mCard.setBack(null);
    }

    /**
     * Method check where it is new card or card is already exists.
     *
     * @return true if card exists, false if card is new.
     */
    public boolean cardExist() {
        return mCard.exists();
    }

    /**
     * Method updates existing card in FB. It is called from AddEditCardActivity
     * on user interaction.
     *
     * @param newFront new front side of card.
     * @param newBack new back side of card.
     * @param onUpdateListener listener on operation completion.
     */
    public void update(final String newFront, final String newBack,
                       final OnOperationCompleteListener onUpdateListener) {
        mCard.setFront(newFront);
        mCard.setBack(newBack);
        mCard.save(onUpdateListener);
    }

    /**
     * Method for adding card to FB. Method is called from AddEditCardActivity
     * on user interaction.
     *
     * @param front text on front side of card.
     * @param back text on back side of card.
     * @param onAddListener listener on operation completion.
     */
    public void add(final String front, final String back,
                    final OnOperationCompleteListener onAddListener) {
        ScheduledCard scheduledCard = new ScheduledCard(mCard.getDeck());
        scheduledCard.setLevel(Level.L0.name());
        scheduledCard.setRepeatAt(System.currentTimeMillis());

        Card newCard = new Card(scheduledCard);
        newCard.setFront(front);
        newCard.setBack(back);

        new MultiWrite()
                .save(newCard)
                .save(scheduledCard)
                .write(onAddListener);
    }
}
