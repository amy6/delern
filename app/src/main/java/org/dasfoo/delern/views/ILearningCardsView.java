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

package org.dasfoo.delern.views;

import org.dasfoo.delern.models.Card;

/**
 * Interface for callbacks from Presenter(LearningCardsActivityPresenter) to
 * View(LearningCardsActivity). Called from Presenter to update user view.
 */
public interface ILearningCardsView {
    /**
     * Handles showing front side of card.
     *
     * @param front text to be shown.
     */
    void showFrontSide(String front);

    /**
     * Handles showing back side of card.
     *
     * @param back text to be shown.
     */
    void showBackSide(String back);

    /**
     * Callback from Presenter to start Activity for editing Cards.
     *
     * @param card card for editing.
     */
    void startEditCardActivity(Card card);

    /**
     * Called when no more cards for learning.
     */
    void finishLearning();

    /**
     * Checks whether back side of card wan shown. It can happened when user
     * by learning cards does editing.
     *
     * @return true if back side was shown. false - if not.
     */
    boolean backSideIsShown();
}
