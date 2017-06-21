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
 *
 */
public interface IDelernMainView {

    /**
     *
     */
    void signIn();

    void learnCardsInDeckClick(Deck deck);

    void editCardsInDeckClick(Deck deck);

    void hideProgressBar();

    void showProgressBar();

    void noDecksMessage(Boolean noDecks);

    void updateUserProfileInfo(User user);
}
