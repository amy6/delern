package org.dasfoo.delern.views;
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

import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.User;

/**
 * Interface for DelernMainActivity that called from Presenter (DelernMainActivityPresenter).
 */
public interface IDelernMainView {

    /**
     * Handles when user is not Signed In.
     */
    void signIn();

    /**
     * Handles when user is going to learn cards.
     *
     * @param deck deck where to learn cards.
     */
    void learnCardsInDeckClick(Deck deck);

    /**
     * Handles when user is going to edit cards in deck.
     *
     * @param deck deck to edit.
     */
    void editCardsInDeckClick(Deck deck);

    /**
     * Handles hiding Progress Bar.
     */
    void hideProgressBar();

    /**
     * Handles showing Progress Bar.
     */
    void showProgressBar();

    /**
     * If user doesn't have decks it shows message.
     *
     * @param noDecks boolean var whether user has decks or not.
     */
    void noDecksMessage(Boolean noDecks);


    /**
     * Updates user profile info if information about user changed.
     *
     * @param user User model.
     */
    void updateUserProfileInfo(User user);
}
