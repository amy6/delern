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

package org.dasfoo.delern.learncards;

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.util.GrammaticalGenderSpecifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.disposables.Disposable;

/**
 * Presenter for LearningCardsActivity. It performs logic with model and
 * updates user view using callbacks
 */
public class LearningCardsActivityPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            LearningCardsActivityPresenter.class);

    private final ILearningCardsView mLearningCardView;
    private Deck mDeck;
    private Card mCard;

    private Disposable mCardAvailableListener;

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
        mCardAvailableListener = mDeck.startScheduledCardWatcher().subscribe(
                card -> {
                    mCard = card;
                    mLearningCardView.showFrontSide(mCard.getFront());
                    // if user decided to edit card, a back side can be shown or not.
                    // After returning back it must show the same state (the same buttons
                    // and text) as before editing
                    if (mLearningCardView.backSideIsShown()) {
                        mLearningCardView.showBackSide(mCard.getBack());
                    }
                },
                error -> {
                    LOGGER.error("Failed to fetch next card", error);
                    mLearningCardView.finishLearning();
                },
                () -> mLearningCardView.finishLearning()
        );
    }

    /**
     * Called from LearningCardsActivity.onStop(). It releases resources.
     */
    public void onStop() {
        mCardAvailableListener.dispose();
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
        // TODO(ksheremet): if card is not loaded yet (e.g. slow database), mCard is null.
        mLearningCardView.showBackSide(mCard.getBack());
    }

    /**
     * Specifies grammatical gender of content.
     *
     * @return gender of content.
     */
    public GrammaticalGenderSpecifier.Gender specifyContentGender() {
        GrammaticalGenderSpecifier.Gender gender;
        try {
            gender = GrammaticalGenderSpecifier.specifyGender(
                    DeckType.valueOf(mCard.getDeck().getDeckType()),
                    mCard.getBack());

        } catch (IllegalArgumentException e) {
            LOGGER.error("Cannot detect gender: {}", mCard.getBack(), e);
            gender = GrammaticalGenderSpecifier.Gender.NO_GENDER;
        }
        return gender;
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
