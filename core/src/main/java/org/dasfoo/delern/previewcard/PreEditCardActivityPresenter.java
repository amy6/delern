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

package org.dasfoo.delern.previewcard;

import org.dasfoo.delern.models.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.disposables.Disposable;

/**
 * Presenter for PreEditCardActivity. It performs logic with model and
 * updates user view using callbacks
 */
public class PreEditCardActivityPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            PreEditCardActivityPresenter.class);

    private final IPreEditCardView mPreEditCardView;
    private Card mCard;
    private Disposable mCardValueEventListener;

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
        if (mCard == null) {
            LOGGER.error("Tried to preview card which doesn't exist");
            return;
        }
        // TODO(dotdoom): create a wrapper around AbstractDataAvailableListener that would show a
        //                toast on error.
        mCardValueEventListener = mCard.watch(Card.class).subscribe((final Card card) -> {
            if (card != null) {
                mCard = card;
                if (mCard.getDeck().isMarkdown()) {
                    mPreEditCardView.showCard(mCard.getFrontHtml(), mCard.getBackHtml(), true,
                            mCard.specifyContentGender());
                } else {
                    mPreEditCardView.showCard(mCard.getFront(), mCard.getBack(), false,
                            mCard.specifyContentGender());
                }
            }
        });
    }

    /**
     * Called from PreEditCardActivity.onStop(). It releases resources.
     */
    public void onStop() {
        if (mCardValueEventListener != null) {
            mCardValueEventListener.dispose();
            mCardValueEventListener = null;
        }
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
        if (mCard != null) {
            mCard.delete();
        }
    }
}
