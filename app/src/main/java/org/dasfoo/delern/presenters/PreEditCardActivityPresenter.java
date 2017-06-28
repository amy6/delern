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

import android.support.annotation.Nullable;

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.views.IPreEditCardView;

/**
 * Presenter for PreEditCardActivity. It performs logic with model and
 * updates user view using callbacks
 */
public class PreEditCardActivityPresenter {

    private Card mCard;
    private AbstractDataAvailableListener<Card> mCardValueEventListener;
    private final IPreEditCardView mPreEditCardView;

    /**
     * Constructor for Presenter. It gets IPreEditCardView as parameter to manage
     * callback to Activity.
     *
     * @param preEditCardView for callback.
     */
    public PreEditCardActivityPresenter(final IPreEditCardView preEditCardView) {
        this.mPreEditCardView = preEditCardView;
    }

    /**
     * Called from PreEditCardActivity.onCreate. It sets card.
     *
     * @param card current card.
     */
    public void onCreate(final Card card) {
        mCard = card;
    }

    /**
     * Called from PreEditCardActivity.onStart(). It shows card to user by using callback method.
     * It sets listener for monitoring updates.
     */
    public void onStart() {
        mPreEditCardView.showCard(mCard.getFront(), mCard.getBack());
        mCardValueEventListener = new AbstractDataAvailableListener<Card>(null) {
            @Override
            public void onData(@Nullable final Card card) {
                if (card != null) {
                    mCard = card;
                    mPreEditCardView.showCard(mCard.getFront(), mCard.getBack());
                }
            }
        };
        mCard.watch(mCardValueEventListener, Card.class);
    }

    /**
     * Called from PreEditCardActivity.onStop(). It releases resources.
     */
    public void onStop() {
        mCardValueEventListener.cleanup();
    }

    /**
     * Calls Activity to edit card.
     */
    public void editCard() {
        mPreEditCardView.startEditCardActivity(mCard);
    }

    /**
     * Deletes card.
     */
    public void deleteCard() {
        mCard.delete();
    }
}
