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

package org.dasfoo.delern.listdecks;

import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.util.IDisposableManager;

/**
 * Interface for DelernMainActivity that called from Presenter (DelernMainActivityPresenter).
 */
public interface IDelernMainView extends IDisposableManager {

    /**
     * Handles when user is not Signed In.
     */
    void signIn();

    /**
     * Handles showing Progress Bar.
     *
     * @param toShow boolean parameter that says whether show Progress Bar or not.
     */
    void showProgressBar(Boolean toShow);

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

    /**
     * Handles adding cards to deck.
     *
     * @param deck deck to add cards
     */
    void addCardsToDeck(Deck deck);
}
