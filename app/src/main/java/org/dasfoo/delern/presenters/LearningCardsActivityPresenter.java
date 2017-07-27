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

import org.dasfoo.delern.controller.CardColor;
import org.dasfoo.delern.controller.GrammaticalGenderSpecifier;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.views.ILearningCardsView;

/**
 * Presenter for LearningCardsActivity. It performs logic with model and
 * updates user view using callbacks
 */
public class LearningCardsActivityPresenter {

    private static final String TAG = LogUtil.tagFor(LearningCardsActivityPresenter.class);

    private final ILearningCardsView mLearningCardView;
    private Deck mDeck;
    private Card mCard;

    private final AbstractDataAvailableListener<Card> mCardAvailableListener =
            new AbstractDataAvailableListener<Card>() {
                @Override
                public void onData(final Card data) {
                    if (data == null) {
                        mLearningCardView.finishLearning();
                        return;
                    }
                    mCard = data;
                    mLearningCardView.showFrontSide(mCard.getFront());
                    // if user decided to edit card, a back side can be shown or not.
                    // After returning back it must show the same state (the same buttons
                    // and text) as before editing
                    if (mLearningCardView.backSideIsShown()) {
                        mLearningCardView.showBackSide(mCard.getBack());
                    }
                }
            };

    /**
     * Constructor. It gets reference to View as parameter for performing callbacks.
     *
     * @param callback reference to View
     */
    public LearningCardsActivityPresenter(final ILearningCardsView callback) {
        this.mLearningCardView = callback;
    }

    /**
     * Called from LearningCardsActivity.onCreate(). It sets
     * deck which cards to learn.
     *
     * @param deck deck which cards to learn.
     */
    public void onCreate(final Deck deck) {
        mDeck = deck;
    }

    /**
     * Called from LearningCardsActivity.onStart(). It starts listener
     * for available cards to learn.
     */
    public void onStart() {
        mDeck.startScheduledCardWatcher(mCardAvailableListener);
    }

    /**
     * Called from LearningCardsActivity.onStop(). It releases resources.
     */
    public void onStop() {
        mCardAvailableListener.cleanup();
    }

    /**
     * Saves data to FB if user knows card.
     */
    public void userKnowCard() {
        mCard.answer(true);
    }

    /**
     * Saves data to FB us user do not know card.
     */
    public void userDoNotKnowCard() {
        mCard.answer(false);
    }

    /**
     * Flip card.
     */
    public void flipCard() {
        mLearningCardView.showBackSide(mCard.getBack());
    }

    /**
     * Specifies grammatical gender of content.
     * Define background color regarding gender.
     *
     * @return id of background color.
     */
    public int setBackgroundCardColor() {
        GrammaticalGenderSpecifier.Gender gender;
        try {
            gender = GrammaticalGenderSpecifier.specifyGender(
                    DeckType.valueOf(mCard.getDeck().getDeckType()),
                    mCard.getBack());

        } catch (IllegalArgumentException e) {
            LogUtil.error(TAG, "Cannot detect gender: " + mCard.getBack(), e);
            gender = GrammaticalGenderSpecifier.Gender.NO_GENDER;
        }
        return CardColor.getColor(gender);
    }

    /**
     * Method delete current card.
     */
    public void delete() {
        mCard.delete();
    }

    /**
     * Perform action if user wants to edit card.
     */
    public void startEditCard() {
        mLearningCardView.startEditCardActivity(mCard);
    }
}
